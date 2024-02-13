package com.ssafy.server.feed.controller;

import com.ssafy.server.api.ApiResponse;
import com.ssafy.server.common.error.ApiException;
import com.ssafy.server.common.error.ApiExceptionFactory;
import com.ssafy.server.feed.error.FeedExceptionEnum;
import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.model.FeedResponse;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private RankService rankService;

    //그냥 전체 Feed 목록 Default를 최신순으로
    @GetMapping("/get/all")
    public ResponseEntity<Page<Feed>> getAllPost(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Page<Feed> recentPageList;
        try{
            Pageable pageable = PageRequest.of(page, size);
            recentPageList = feedService.sortRecentFeedList(pageable);
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_SORT_ERROR));
        }
        return ResponseEntity.ok(recentPageList);
    }

    @GetMapping("/get/old")
    public ResponseEntity<Page<Feed>> getOldPost(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Page<Feed> oldPageList;
        try {
            Pageable pageable = PageRequest.of(page, size);
            oldPageList = feedService.sortOldFeedList(pageable);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_SORT_ERROR));
        }
        return ResponseEntity.ok(oldPageList);
    }

    @GetMapping("/get/top100")
    public ResponseEntity<List<FeedStatsDocument>> getTop100(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        List<FeedStatsDocument> topList;
        try {
            topList = rankService.getTop100Ranking();
            if (!topList.isEmpty()) {
                topList = paginate(topList, page, size);
            }
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_NOT_FOUND));
        }
        return ResponseEntity.ok(topList);
    }

    @GetMapping("/getUser")
    public ResponseEntity<ApiResponse<List<Feed>>> getFeedByUser(@CookieValue String uuid) {
        List<Feed> feeds = null;
        try {
            feeds = feedService.getFeedByUserPk(userService.getUserPk(UUID.fromString(uuid)));
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_NOT_FOUND));
        }
        return new ResponseEntity<>(ApiResponse.success(feeds), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/{feedId}")
    public ResponseEntity<Feed> getPostById(@PathVariable int feedId) {
        Feed post;
        try {
            post = feedService.getFeedById(feedId);
        } catch (Exception e){
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_NOT_FOUND));
        }
        return ResponseEntity.ok(post);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createPost(@RequestBody Feed feed) {
        Feed createdPost;
        try {
            createdPost = feedService.createFeed(feed);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_CREATION_FAILED));
        }
        return new ResponseEntity<>(ApiResponse.success(createdPost), HttpStatus.ACCEPTED);
    }

    @PostMapping("/update/{feedId}")
    public ResponseEntity<ApiResponse<?>> updatePost(@PathVariable int feedId, @RequestBody Feed updatedPost) {
        Feed post;
        try {
            post = feedService.updateFeed(feedId, updatedPost);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(FeedExceptionEnum.FEED_UPDATE_FAILED));
        }
        return new ResponseEntity<>(ApiResponse.success(post), HttpStatus.ACCEPTED);
    }

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
