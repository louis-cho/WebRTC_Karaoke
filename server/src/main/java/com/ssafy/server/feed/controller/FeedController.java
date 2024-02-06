package com.ssafy.server.feed.controller;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;


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
}
