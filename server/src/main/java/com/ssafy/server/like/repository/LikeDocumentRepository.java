package com.ssafy.server.like.repository;

import com.ssafy.server.like.document.LikesDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface LikeDocumentRepository extends ElasticsearchRepository<LikesDocument, Integer> {

}