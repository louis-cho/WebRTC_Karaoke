package com.ssafy.server.feed.service;


import com.ssafy.server.feed.entity.Feed;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FeedServiceImpl implements FeedService {
    private final List<Feed> feedList = new ArrayList<>();

    @Override
    public Feed getFeedById(int feedId) {
        Optional<Feed> optionalFeed = feedList.stream()
                .filter(feed -> feed.getFeedId() == feedId)
                .findFirst();

        return optionalFeed.orElse(null);
    }

    @Override
    public Feed createFeed(Feed newFeed) {
        // Generate a unique feedId (for demonstration purposes)
        newFeed.setFeedId(feedList.size() + 1);

        feedList.add(newFeed);
        return newFeed;
    }

    @Override
    public Feed updateFeed(int feedId, Feed updatedFeed) {
        for (int i = 0; i < feedList.size(); i++) {
            Feed existingFeed = feedList.get(i);
            if (existingFeed.getFeedId() == feedId) {
                // Update fields with the new values
                existingFeed.setUserPk(updatedFeed.getUserPk());
                existingFeed.setSongId(updatedFeed.getSongId());
                existingFeed.setContent(updatedFeed.getContent());
                existingFeed.setThumbnailUrl(updatedFeed.getThumbnailUrl());
                existingFeed.setVideoUrl(updatedFeed.getVideoUrl());
                existingFeed.setVideoLength(updatedFeed.getVideoLength());
                existingFeed.setStatus(updatedFeed.getStatus());
                existingFeed.setTotalPoint(updatedFeed.getTotalPoint());

                return existingFeed;
            }
        }
        return null;
    }

    @Override
    public boolean deleteFeed(int feedId) {
        return feedList.removeIf(feed -> feed.getFeedId() == feedId);
    }
}
