package com.ssafy.server.cache.elastic.repository;

import com.ssafy.server.like.document.LikesDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface LikeSearchRepository extends ElasticsearchRepository<LikesDocument, Integer> {
    LikesDocument findById(int likeid);
}