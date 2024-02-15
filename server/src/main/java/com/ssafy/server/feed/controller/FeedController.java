package com.ssafy.server.feed.controller;

import com.ssafy.server.api.ApiResponse;
import com.ssafy.server.common.error.ApiException;
import com.ssafy.server.common.error.ApiExceptionFactory;
import com.ssafy.server.feed.error.FeedExceptionEnum;
import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.model.FeedResponse;
import com.ssafy.server.feed.model.FeedResponseFactory;
import com.ssafy.server.feed.rank.document.FeedStatsDocument;
import com.ssafy.server.feed.rank.service.RankService;
import com.ssafy.server.feed.service.FeedService;
import com.ssafy.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private RankService rankService;

    /**
     * feed list를 페이지네이션을 통해 반환한다.
     * @param page fetch 시작 페이지
     * @param size fetch 사이즈
     * @return ResponseEntity<List<FeedResponse>>
     */
    @GetMapping("/get/all")
    public ResponseEntity<List<FeedResponse>> getAllPost(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Page<Feed> recentPageList;
        List<FeedResponse> response;
        try{
            Pageable pageable = PageRequest.of(page, size);
            recentPageList = feedService.sortRecentFeedList(pageable);

            response = recentPageList.getContent()
                    .stream()
                    .map(feed -> {
                        int userPk = -1;
                        if(feed.getUserPk() == null)
                            return null;
                        userPk = feed.getUserPk();
                        UUID uuid = null;
                        try {
                            uuid = userService.getUUIDByUserPk(userPk);
                        } catch (Exception e) {
                            return null;
                        }
                        if(uuid == null) {
                            return null; // 또는 원하는 다른 값으로 대체
                        }
                        return FeedResponseFactory.createFeedResponseFromFeed(feed, uuid);
                    })
                    .filter(Objects::nonNull) // null 값 필터링
                    .collect(Collectors.toList());

        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_SORT_ERROR));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/old")
    public ResponseEntity<List<FeedResponse>> getOldPost(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Page<Feed> oldPageList;
        List<FeedResponse> response;
        try {
            Pageable pageable = PageRequest.of(page, size);
            oldPageList = feedService.sortOldFeedList(pageable);

            response = oldPageList.getContent()
                    .stream()
                    .map(feed -> {
                        int userPk = -1;
                        if(feed.getUserPk() == null)
                            return null;
                        userPk = feed.getUserPk();
                        UUID uuid = null;
                        try {
                            uuid = userService.getUUIDByUserPk(userPk);
                        } catch (Exception e) {
                            return null;
                        }
                        if(uuid == null) {
                            return null; // 또는 원하는 다른 값으로 대체
                        }
                        return FeedResponseFactory.createFeedResponseFromFeed(feed, uuid);
                    })
                    .filter(Objects::nonNull) // null 값 필터링
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_SORT_ERROR));
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/top100")
    public ResponseEntity<List<FeedResponse>> getTop100(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "100") int size){
        List<FeedResponse> response;
        try {
            List<FeedStatsDocument> topList = rankService.getTop100Ranking();

            // 특정 페이지에 해당하는 피드 가져오기
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, topList.size());
            List<FeedResponse> feedResponses = new ArrayList<>();

            for (int i = startIndex; i < endIndex; i++) {
                FeedStatsDocument feedStatsDocument = topList.get(i);
                int feedId = feedStatsDocument.getFeedId();

                // 피드 ID로 피드 가져오기
                Feed feed = feedService.getFeedById(feedId);

                if(feed == null)
                    continue;
                // 유저 UUID 가져오기
                UUID userUuid = userService.getUUIDByUserPk(feed.getUserPk());

                // FeedResponse 생성
                FeedResponse feedResponse = FeedResponseFactory.createFeedResponseFromFeed(feed, userUuid);
                feedResponses.add(feedResponse);
            }

            response = feedResponses;
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_NOT_FOUND));
        }
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 유저의 작성 피드 목록을 반환한다.
     * @param uuid 유저 uuid
     * @return 피드 리스트
     */
    @GetMapping("/getUser/{uuid}")
    public ResponseEntity<ApiResponse<List<FeedResponse>>> getFeedByUser(@PathVariable String uuid) {
        List<FeedResponse> feeds = null;
        try {
            feeds = feedService.getFeedByUserPk(userService.getUserPk(UUID.fromString(uuid)));
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_NOT_FOUND));
        }
        return new ResponseEntity<>(ApiResponse.success(feeds), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/{feedId}")
    public ResponseEntity<FeedResponse> getPostById(@PathVariable int feedId) {
        Feed post;
        FeedResponse response = null;
        try {
            post = feedService.getFeedById(feedId);
            response = FeedResponseFactory.createFeedResponseFromFeed(post, userService.getUser(post.getUserPk()).getUserKey());
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_NOT_FOUND));
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createPost(@RequestHeader(name = "uuid") String uuid, @RequestBody Feed feed) {
        Feed createdPost;
        int userPk = -1;
        try {
            userPk = userService.getUserPk(UUID.fromString(uuid));
            feed.setUserPk(userPk);
            createdPost = feedService.createFeed(feed);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_CREATION_FAILED));
        }
        return new ResponseEntity<>(ApiResponse.success(createdPost), HttpStatus.ACCEPTED);
    }

    @PostMapping("/update/{feedId}")
    public ResponseEntity<ApiResponse<?>> updatePost(@PathVariable int feedId, @RequestBody FeedResponse updatedPost) {
        Feed post;
        try {
            post = feedService.updateFeed(feedId, updatedPost);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_UPDATE_FAILED));
        }
        return new ResponseEntity<>(ApiResponse.success(post), HttpStatus.ACCEPTED);
    }

    /**
     * 피드 아이디에 해당하는 피드를 삭제한다.
     * @param feedId 삭제할 피드의 아이디
     * @return 성공 여부
     */
    @PostMapping("/delete/{feedId}")
    public ResponseEntity<Boolean> deletePost(@PathVariable int feedId) {
        boolean result;
        try {
            result = feedService.deleteFeed(feedId);
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_DELETE_FAILED));
        }

        if (result) {
            // 삭제가 성공한 경우
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            // 삭제가 실패한 경우 (해당 feedId를 찾지 못한 경우 등)
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    private List<FeedStatsDocument> paginate(List<FeedStatsDocument> dataList, int page, int size) {
        int totalSize = dataList.size();
        int startIndex = (page - 1) * size;
        int endIndex = Math.min(startIndex + size, totalSize);

        if (startIndex >= totalSize) {
            return Collections.emptyList(); // 페이지 범위를 벗어나면 빈 리스트 반환
        }

        return dataList.subList(startIndex, endIndex);
    }
}
