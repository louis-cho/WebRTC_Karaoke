package com.ssafy.server.cache.elastic.repository;

import com.ssafy.server.feed.rank.document.FeedStatsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FeedStatsSearchRepository extends ElasticsearchRepository<FeedStatsDocument, Integer> {
    FeedStatsDocument findById(int feedStatsId);
}