package com.ssafy.server.feed.rank.service;

import com.ssafy.server.feed.rank.document.FeedStatsDocument;

import java.util.List;

public interface RankService {
    public List<FeedStatsDocument> getTop100Ranking();
}
