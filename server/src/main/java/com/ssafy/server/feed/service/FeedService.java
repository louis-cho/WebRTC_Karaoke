package com.ssafy.server.feed.service;
import com.ssafy.server.feed.model.Feed;

import java.util.List;

public interface FeedService {

    Feed getFeedById(int postId);

    Feed createFeed(Feed post);

    Feed updateFeed(int postId, Feed updatedPost);

    boolean deleteFeed(int postId);
}
