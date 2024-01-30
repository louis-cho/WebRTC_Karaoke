package com.ssafy.server.feed.controller;

import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.repository.FeedRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/feed")
public class FeedController {

    @Autowired
    private FeedRepository feedRepository;



    @PostMapping("/create")
    public Feed createFeed(@RequestBody Feed feed) {
        return feedRepository.save(feed);
    }

    @GetMapping("/get/{id}")
    public Feed getFeedById(@PathVariable int id) {
        return feedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Feed ID: " + id));
    }

    @PostMapping("/delete")
    public boolean deleteFeed(@RequestBody int feedId) {
        int ret = feedRepository.deleteById(feedId);
        if(ret > 0) {
            return true;
        } else {
            return false;
        }
    }
}
