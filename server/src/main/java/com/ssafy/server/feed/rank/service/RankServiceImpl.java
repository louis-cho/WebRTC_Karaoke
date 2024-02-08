package com.ssafy.server.feed.rank.service;

import com.ssafy.server.feed.rank.document.FeedStatsDocument;
import com.ssafy.server.hit.document.HitDocument;
import com.ssafy.server.like.document.LikesDocument;
import lombok.Getter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

@Service
public class RankServiceImpl implements RankService {

    private final RestHighLevelClient elasticsearchClient;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Getter
    private List<FeedStatsDocument> top100Ranking = new ArrayList<>();

    private String lastTime = null;

    @Autowired
    public RankServiceImpl(RestHighLevelClient elasticsearchClient, ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchClient = elasticsearchClient;
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }
    @Scheduled(fixedDelay = 10000) // 24시간마다 실행
    public void calculateRank() {
        String likesIndexName = "likes"; // like 인덱스
        String hitIndexName = "hits"; // hit 인덱스

        // likes 데이터 가져오기
        List<SearchHit> likesData = fetchDataFromElasticsearch(likesIndexName);

        // hit 데이터 가져오기
        List<SearchHit> hitsData = fetchDataFromElasticsearch(hitIndexName);

        // 각 게시글 아이디 별로 점수 계산
        calculateScores(likesData, hitsData);

        // 상위 100개 게시글만 선택
        updateTop100Ranking();
    }

    private void updateTop100Ranking() {
        // Sort feed_stats based on scores and select top 100
        try {
            top100Ranking = elasticsearchRestTemplate.search(
                            new NativeSearchQueryBuilder()
                                    .withPageable(PageRequest.of(0, 100, Sort.by(Sort.Order.desc("score"))))
                                    .build(), FeedStatsDocument.class)
                    .stream()
                    .map(searchHit -> (FeedStatsDocument) searchHit.getContent())
                    .collect(Collectors.toList());
        } catch(Exception e) {
            return;
        }
    }

    private List<SearchHit> fetchDataFromElasticsearch(String index) {
        List<SearchHit> searchData = new ArrayList<>();

        try {
            SearchRequest searchRequest = new SearchRequest(index);

            // 정렬 및 범위 조건 추가
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if (lastTime != null) {
                searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchAllQuery())
                        .filter(QueryBuilders.rangeQuery("timestamp").gt(lastTime)));
                searchSourceBuilder.sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC));
            } else {
                searchSourceBuilder.query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchAllQuery()));
                searchSourceBuilder.sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC));
            }

            searchRequest.source(searchSourceBuilder);

            SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
            org.elasticsearch.search.SearchHits hits = searchResponse.getHits();

            // 최신 데이터를 찾아 lastTime 업데이트
            for (SearchHit hit : hits) {
                searchData.add(hit);

                // 최신 데이터의 timestamp을 lastTime으로 업데이트
                String timestamp = hit.getSourceAsMap().get("timestamp").toString();
                if (lastTime == null || timestamp.compareTo(lastTime) > 0) {
                    lastTime = timestamp;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return searchData;
    }



    private void calculateScores(List<SearchHit> likesData, List<SearchHit> hitsData) {
        // 각 게시글 아이디 별로 점수 계산 로직
        // 이 부분은 실제 프로젝트의 비즈니스 로직에 맞게 수정해야 합니다.
        // 예시로 likesData와 hitsData에서 게시글 아이디를 추출하고, 점수를 계산하는 방식을 보여드립니다.

        List<LikesDocument> likesDocuments = likesData.stream()
                .map(hit -> {
                    Map<String, Object> source = hit.getSourceAsMap();

                    // 필드 값 가져오기
                    int likeId = (int) source.get("like_id");
                    int feedId = (int) source.get("feed_id");
                    int userPk = (int) source.get("user_pk");
                    boolean status = Boolean.TRUE.equals(source.get("status"));

                    // LikesDocument 객체 생성
                    LikesDocument likesDocument = new LikesDocument();
                    likesDocument.setFeedId(feedId);
                    likesDocument.setLikeId(likeId);
                    likesDocument.setUserPk(userPk);
                    likesDocument.setStatus(status);

                    return likesDocument;
                })
                .collect(Collectors.toList());


        List<HitDocument> hitDocuments = hitsData.stream()
                .map(hit -> {
                    Map<String, Object> source = hit.getSourceAsMap();

                    // 필드 값 가져오기
                    int hitId = (int) source.get("hit_id");
                    int feedId = (int) source.get("feed_id");
                    int userPk = (int) source.get("user_pk");

                    // LikesDocument 객체 생성
                    HitDocument hitDocument = new HitDocument();
                    hitDocument.setHitId(hitId);
                    hitDocument.setFeedId(feedId);
                    hitDocument.setUserPk(userPk);

                    return hitDocument;
                })
                .collect(Collectors.toList());

        // feedId 별로 likes와 hit를 묶어줌
        Map<Integer, List<LikesDocument>> likesByFeedId = likesDocuments.stream()
                .collect(Collectors.groupingBy(LikesDocument::getFeedId));

        Map<Integer, List<HitDocument>> hitsByFeedId = hitDocuments.stream()
                .collect(Collectors.groupingBy(HitDocument::getFeedId));


        updateFeedStats(likesByFeedId, hitsByFeedId);


    }

    private void updateFeedStats(Map<Integer, List<LikesDocument>> likesByFeedId, Map<Integer, List<HitDocument>> hitsByFeedId) {
        Set<Integer> uniqueFeedIds = new HashSet<>();
        uniqueFeedIds.addAll(likesByFeedId.keySet());
        uniqueFeedIds.addAll(hitsByFeedId.keySet());

        List<FeedStatsDocument> feedStatsList = new ArrayList<>();

        for (Integer feedId : uniqueFeedIds) {
            List<LikesDocument> likesList = likesByFeedId.getOrDefault(feedId, Collections.emptyList());
            List<HitDocument> hitsList = hitsByFeedId.getOrDefault(feedId, Collections.emptyList());

            // Calculate total likes and hits
            int totalLikes = likesList.size();
            int totalHits = hitsList.size();

            // Calculate score (modify this based on your business logic)
            double score = calculateScore(totalLikes, totalHits);

            // Create FeedStatsDocument and set fields
            FeedStatsDocument feedStatsDocument = new FeedStatsDocument();
            feedStatsDocument.setFeedId(feedId);
            feedStatsDocument.setLikes(totalLikes);
            feedStatsDocument.setViews(totalHits);
            feedStatsDocument.setScore(score);

            feedStatsList.add(feedStatsDocument);
        }

        // Elasticsearch에 feed_stats 업데이트
        updateFeedStatsInElasticsearch(feedStatsList);
    }

    private void updateFeedStatsInElasticsearch(List<FeedStatsDocument> feedStatsList) {
        for (FeedStatsDocument newFeedStats : feedStatsList) {
            int feedId = newFeedStats.getFeedId();

            // Check if the feed_stats already exist in Elasticsearch
            Optional<FeedStatsDocument> existingFeedStats = fetchFeedStatsByIdFromElasticsearch(feedId);

            if (existingFeedStats.isPresent()) {
                // If it exists, update the scores (modify this based on your business logic)
                FeedStatsDocument currentStats = existingFeedStats.get();
                currentStats.setLikes(newFeedStats.getLikes() + currentStats.getLikes());
                currentStats.setViews(newFeedStats.getViews() + currentStats.getViews());
                currentStats.setScore(newFeedStats.getScore() + currentStats.getScore());

                // Save the updated feed_stats back to Elasticsearch
                saveFeedStatsToElasticsearch(currentStats);
            } else {
                // If it doesn't exist, add the new feed_stats to Elasticsearch
                saveFeedStatsToElasticsearch(newFeedStats);
            }
        }
    }

    private void saveFeedStatsToElasticsearch(FeedStatsDocument feedStatsDocument) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(String.valueOf(feedStatsDocument.getFeedId()))
                .withObject(feedStatsDocument)
                .build();

        elasticsearchRestTemplate.index(indexQuery, IndexCoordinates.of("feed_stats"));
    }

    private Optional<FeedStatsDocument> fetchFeedStatsByIdFromElasticsearch(int feedId) {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termQuery("feedId", feedId))
                .build();

        SearchHits<FeedStatsDocument> searchHits = null;
        try {
            searchHits = elasticsearchRestTemplate.search(searchQuery, FeedStatsDocument.class);
        } catch(Exception e) {
            elasticsearchRestTemplate.indexOps(FeedStatsDocument.class).create();
        }
        if (searchHits != null && !searchHits.isEmpty()) {
            return Optional.of(searchHits.getSearchHit(0).getContent());
        } else {
            return Optional.empty();
        }
    }

    private double calculateScore(int likesCount, int hitsCount) {
        // 실제 프로젝트의 비즈니스 로직에 따라 점수 계산 방식을 수정해주세요.
        // 예시로 likesCount와 hitsCount를 가중치를 곱하여 합산하는 방식을 사용하였습니다.
        return likesCount * 5 + hitsCount * 3.0;
    }

}
