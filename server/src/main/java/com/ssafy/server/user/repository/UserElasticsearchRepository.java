package com.ssafy.server.user.repository;

import com.ssafy.server.user.document.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, Integer> {
    List<UserDocument> findByNickname(String nickname);
}