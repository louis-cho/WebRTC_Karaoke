package com.ssafy.server.hit.repository;

import com.ssafy.server.hit.document.HitDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface HitDocumentRepository extends ElasticsearchRepository<HitDocument, Long> {
}