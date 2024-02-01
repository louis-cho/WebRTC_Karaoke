package com.ssafy.server.feed.rank.service;

import com.ssafy.server.feed.rank.document.FeedStatsDocument;
import com.ssafy.server.hit.document.HitDocument;
import com.ssafy.server.like.document.LikesDocument;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;
@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    private List<FeedStatsDocument> top100Ranking = new ArrayList<>();

    public List<FeedStatsDocument> getTop100Ranking() {
        return top100Ranking;
    }

    @Scheduled(cron = "0/10 * * * * *") // 매일 자정에 실행
    public void calculateRank() {
        // Elasticsearch에서 likes 테이블 데이터 가져오기
        SearchHits<LikesDocument> likesData = fetchDataFromElasticsearch("likes", LikesDocument.class);

        // Elasticsearch에서 hits 테이블 데이터 가져오기
        SearchHits<HitDocument> hitsData = fetchDataFromElasticsearch("hit", HitDocument.class);

        // likes 데이터를 FeedId별로 그룹화
        Map<Integer, List<SearchHit<LikesDocument>>> groupedLikesData = likesData.stream()
                .collect(Collectors.groupingBy(hit -> hit.getContent().getFeedId()));

        // hits 데이터를 FeedId별로 그룹화
        Map<Integer, List<SearchHit<HitDocument>>> groupedHitsData = hitsData.stream()
                .collect(Collectors.groupingBy(hit -> hit.getContent().getFeedId()));

        // 각 피드 아이디에 대한 통계 계산 및 Elasticsearch에 저장
        for (Integer feedId : groupedLikesData.keySet()) {
            List<SearchHit<LikesDocument>> feedLikesData = groupedLikesData.get(feedId);
            List<SearchHit<HitDocument>> feedHitsData = groupedHitsData.get(feedId);

            // 간단한 통계 계산
            int likesCount = 0, viewsCount = 0;
            if(feedLikesData != null)
                likesCount = feedLikesData.size();
            if(feedHitsData != null)
                viewsCount = feedHitsData.size();
            double score = likesCount * 5 + viewsCount * 3;

            // 계산된 통계를 Elasticsearch에 저장
            FeedStatsDocument statsEntity = new FeedStatsDocument();
            statsEntity.setFeedId(feedId);
            statsEntity.setLikes(likesCount);
            statsEntity.setViews(viewsCount);
            statsEntity.setScore(score);

            elasticsearchTemplate.save(statsEntity);
        }
        // 상위 100개 문서를 가져오는 쿼리 생성
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchAllQuery())
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withPageable(org.springframework.data.domain.PageRequest.of(0, 100))
                .build();

        // 상위 100개 문서 가져오기
        SearchHits<FeedStatsDocument> searchHits = elasticsearchTemplate.search(searchQuery, FeedStatsDocument.class);
        top100Ranking = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        System.out.println(top100Ranking);
    }

//    private <T> SearchHits<T> fetchDataFromElasticsearch(String index, Class<T> documentClass) {
//        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchAllQuery())
//                .build();
//
//
//        return elasticsearchTemplate.search(searchQuery, documentClass);
//    }

    private <T> SearchHits<T> fetchDataFromElasticsearch(String index, Class<T> documentClass) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // 인덱스 필터링 추가
        // queryBuilder.withFilter(QueryBuilders.termQuery("_index", index)).withQuery(matchAllQuery());
        queryBuilder.withQuery(matchAllQuery());
        NativeSearchQuery searchQuery = queryBuilder.build();

        return elasticsearchTemplate.search(searchQuery, documentClass);
    }
}