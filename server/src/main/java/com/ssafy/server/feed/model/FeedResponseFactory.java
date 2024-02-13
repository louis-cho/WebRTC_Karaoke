package com.ssafy.server.feed.model;

import java.util.UUID;

public class FeedResponseFactory {

    public static FeedResponse createFeedResponseFromFeed(Feed feed, UUID userUUID) {
        FeedResponse feedResponse = new FeedResponse();

        feedResponse.setFeedId(feed.getFeedId());
        feedResponse.setUserUUID(userUUID);
        feedResponse.setSongId(feed.getSongId());
        feedResponse.setTitle(feed.getTitle());
        feedResponse.setContent(feed.getContent());
        feedResponse.setThumbnailUrl(feed.getThumbnailUrl());
        feedResponse.setVideoUrl(feed.getVideoUrl());
        feedResponse.setVideoLength(feed.getVideoLength());
        feedResponse.setStatus(feed.getStatus());
        feedResponse.setTotalPoint(feed.getTotalPoint());
        feedResponse.setTime(feed.getTime());

        return feedResponse;
    }
}