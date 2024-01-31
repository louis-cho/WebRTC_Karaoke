package com.ssafy.server.cache.elastic;

import com.ssafy.server.cache.elastic.repository.FeedStatsSearchRepository;
import com.ssafy.server.cache.elastic.repository.LikeSearchRepository;
import com.ssafy.server.feed.rank.document.FeedStatsDocument;
import com.ssafy.server.like.document.LikesDocument;
import com.ssafy.server.like.model.Likes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private LikeSearchRepository likeSearchRepository;

    @Autowired
    private FeedStatsSearchRepository feedStatsSearchRepository;


    public void updateLikeStatistics(Likes like) {

        // 기존 like 정보 fetch
        LikesDocument fetchedDocument = likeSearchRepository.findById(like.getLikeId());
        
        // 새로운 like 정보 설정
        LikesDocument likesDocument = new LikesDocument();
        likesDocument.setLikeId(like.getLikeId());
        likesDocument.setFeedId(like.getFeedId());
        likesDocument.setUserPk(like.getUserPk());
        likesDocument.setStatus(like.getStatus());


        updateFeedStatsStaticstics(likesDocument, fetchedDocument);  // feed 통계 업데이트
        if(fetchedDocument == null) {   // 기존 like 정보가 없는 경우 바로 저장
            likeSearchRepository.save(likesDocument);
        } else {
            if(fetchedDocument.getStatus() == likesDocument.getStatus()) {   // 이전 상황과 같은 정보는 무시
                return;
            } else {    // 업데이트
                fetchedDocument.setStatus(likesDocument.getStatus());
                likeSearchRepository.save(fetchedDocument);
            }
        }
    }

    public void updateFeedStatsStaticstics(LikesDocument newLike, LikesDocument fetched) {
        FeedStatsDocument feedStatsDocument = feedStatsSearchRepository.findById(newLike.getFeedId());
        
        if(feedStatsDocument == null) { // 첫 조회
            feedStatsDocument = new FeedStatsDocument();
            feedStatsDocument.init();

            feedStatsDocument.setFeedId(newLike.getFeedId());
            if(newLike.getStatus() == 'O') {
                feedStatsDocument.increaseLike();
            }
        } else {
            if(newLike.getStatus() == fetched.getStatus()) {
                return;
            } else {
                if(newLike.getStatus() == 'X') {
                    feedStatsDocument.decreaseLike();
                } else {
                    feedStatsDocument.increaseLike();
                }
            }
        }

        feedStatsSearchRepository.save(feedStatsDocument);
    }

}
