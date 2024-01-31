package com.ssafy.server.feed.service;
import com.ssafy.server.feed.model.Feed;

import java.util.List;

public interface FeedService {

    Feed getFeedById(int feedId);

    Feed createFeed(Feed feed);

    Feed updateFeed(int feedId, Feed updatedFeed);

    boolean deleteFeed(int feedId);
}
