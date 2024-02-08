package com.ssafy.server.feed.controller;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.rank.document.FeedStatsDocument;
import com.ssafy.server.feed.rank.service.RankService;
import com.ssafy.server.feed.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    @Autowired
    private RankService rankService;

    @GetMapping("/get/all")
    public ResponseEntity<Page<Feed>> getAllPost(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feedService.getAllFeedList(pageable));
    }

    @GetMapping("/get/recent")
    public ResponseEntity<Page<Feed>> getRecentPost(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feedService.sortRecentFeedList(pageable));
    }

    @GetMapping("/get/old")
    public ResponseEntity<Page<Feed>> getOldPost(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(feedService.sortOldFeedList(pageable));
    }

    @GetMapping("/get/top100")
    public ResponseEntity<List<FeedStatsDocument>> getTop100(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size){
        List<FeedStatsDocument> topList = rankService.getTop100Ranking();
        if(!topList.isEmpty()){
            topList = paginate(topList, page, size);
        }
        return ResponseEntity.ok(topList);
    }

    @GetMapping("/get/{feedId}")
    public ResponseEntity<Feed> getPostById(@PathVariable int feedId) {
        Feed post = feedService.getFeedById(feedId);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/create")
    public ResponseEntity<Feed> createPost(@RequestBody Feed feed) {
        Feed createdPost = feedService.createFeed(feed);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Feed> updatePost(@PathVariable int feedId, @RequestBody Feed updatedPost) {
        Feed post = feedService.updateFeed(feedId, updatedPost);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> deletePost(@PathVariable int feedId) {
        boolean result = feedService.deleteFeed(feedId);

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
