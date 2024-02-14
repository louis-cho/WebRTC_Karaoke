package com.ssafy.server.feed.service;
import com.ssafy.server.feed.model.Feed;
import com.ssafy.server.feed.model.FeedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {

    Page<Feed> getAllFeedList(Pageable pageable);

    Page<Feed> sortRecentFeedList(Pageable pageable);

    Page<Feed> sortOldFeedList(Pageable pageable);

    Feed getFeedById(int feedId);

    Feed createFeed(Feed feed);

    Feed updateFeed(int feedId, FeedResponse updatedFeed);

    boolean deleteFeed(int feedId);

    List<FeedResponse> getFeedByUserPk(int userPk);
}
