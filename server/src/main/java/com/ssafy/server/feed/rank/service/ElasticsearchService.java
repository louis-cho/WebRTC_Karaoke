package com.ssafy.server.feed.rank.service;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ElasticsearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    // 동기화 시간을 저장할 변수 (실제로는 데이터베이스 또는 외부 저장소에 저장해야 함)
    private long lastSyncTimestamp = 0;

    @Autowired
    public ElasticsearchService(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    // 주기적으로 동기화 작업 실행
    @Scheduled(fixedRate = 60000) // 예시: 1분마다 실행
    public void synchronizeData() {
        // 최신 데이터를 가져오기 위한 기준 시간 설정
        long currentTime = System.currentTimeMillis();
        long startTime = lastSyncTimestamp;

        // "like" 인덱스 동기화
        synchronizeIndex("like", startTime, currentTime);

        // "hit" 인덱스 동기화
        synchronizeIndex("hit", startTime, currentTime);

        // 동기화가 끝난 시간을 갱신
        lastSyncTimestamp = currentTime;
    }

    private void synchronizeIndex(String indexName, long startTime, long endTime) {
        // 변경된 데이터만 가져오기 위한 쿼리 설정
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.rangeQuery("timestamp").gte(startTime).lt(endTime))
                .build();

        // Elasticsearch에서 변경된 데이터 조회
        SearchHits<Map> searchHits = elasticsearchRestTemplate.search(query, Map.class, IndexCoordinates.of(indexName));

        // 변경된 데이터를 처리하는 로직 추가 (예시: 실제로는 데이터를 업데이트 또는 삽입)
        processSynchronizedData(searchHits);

        // 추가로 필요한 작업 수행 (예: 로깅, 에러 처리 등)
    }

    private void processSynchronizedData(SearchHits<Map> searchHits) {
        // 변경된 데이터를 처리하는 로직 추가 (예시: 실제로는 데이터를 업데이트 또는 삽입)
        // ...

        // 예시: 변경된 데이터 로그 출력
        for (SearchHit<Map> searchHit : searchHits) {
            System.out.println("Synchronized data: " + searchHit.getContent());
        }
    }

    public List<Map<String, Object>> searchLikeData() {
        // "like" 인덱스에서 모든 문서를 가져오는 쿼리
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();

        // Elasticsearch에서 최신 데이터 조회
        SearchHits<Map> searchHits = elasticsearchRestTemplate.search(query, Map.class, IndexCoordinates.of("like"));

        // 최신 데이터를 저장할 리스트
        List<Map<String, Object>> result = new ArrayList<>();

        // 최신 데이터를 리스트에 추가
        for (SearchHit<Map> searchHit : searchHits) {
            result.add(searchHit.getContent());
        }

        // 최종 결과 반환
        return result;
    }

    public List<Map<String, Object>> searchHitData() {
        // "hit" 인덱스에서 모든 문서를 가져오는 쿼리
        Query query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchAllQuery())
                .build();

        // Elasticsearch에서 최신 데이터 조회
        SearchHits<Map> searchHits = elasticsearchRestTemplate.search(query, Map.class, IndexCoordinates.of("hit"));

        // 최신 데이터를 저장할 리스트
        List<Map<String, Object>> result = new ArrayList<>();

        // 최신 데이터를 리스트에 추가
        for (SearchHit<Map> searchHit : searchHits) {
            result.add(searchHit.getContent());
        }

        // 최종 결과 반환
        return result;
    }
}
