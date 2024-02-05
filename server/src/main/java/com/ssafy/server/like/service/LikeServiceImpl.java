package com.ssafy.server.like.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.like.model.LikeStat;
import com.ssafy.server.like.repository.LikeRepository;
import com.ssafy.server.like.repository.LikeStatRepository;
import com.ssafy.server.syncdata.LikeSyncData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {
    private static final String LIKE_HASH_KEY = "likes";
    @Autowired
    private final LikeRepository likeRepository;
    @Autowired
    private final LikeStatRepository likeStatRepository;

    @Autowired
    private final RedisTemplate<String, LikeSyncData> redisTemplate;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, LikeStatRepository likeStatRepository, RedisTemplate<String, LikeSyncData> redisTemplate) {
        this.likeRepository = likeRepository;
        this.likeStatRepository = likeStatRepository;
        this.redisTemplate = redisTemplate;
    }

    public void save(int feedId, int userPk) {
        LikeSyncData likeSyncData = new LikeSyncData();

        likeSyncData.setFeedId(feedId);
        likeSyncData.setUserPk(userPk);
        likeSyncData.setStatus(true);

        // 모든 내역은 캐시에 먼저 저장한다
        redisTemplate.opsForHash().put(LIKE_HASH_KEY, getHashKey(likeSyncData), likeSyncData);
    }

    public LikeSyncData findById(int feedId, int userPk) {
        List<LikeSyncData> list = likeRepository.findByUserPkAndFeedId(userPk, feedId); // DB 데이터 반환
        if (list.size() == 0) {  // DB에 없으면 redis에서 반환
            return (LikeSyncData) redisTemplate.opsForHash().get(LIKE_HASH_KEY, getHashKey(feedId, userPk));
        }
        return list.get(0);
    }

    public int findAllByFeedId(int feedId) {

        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<Object, Object> result = new HashMap<>();

        hashOperations.scan(LIKE_HASH_KEY, ScanOptions.scanOptions().match("*" + feedId + "_*").build())
                .forEachRemaining(entry -> {
                    System.out.println(entry.getValue());
                    result.put(entry.getKey(), entry.getValue());
                });
        
        int cacheSize = result.size();  // 캐시 사이즈는 무조건 안맞을 수 밖에 없음 (좋아요 취소까지 계산하도록 함)
        
        if (likeStatRepository.findById(feedId).isPresent()) {  // DB 집계 테이블 내역도 가져오기
            return cacheSize + likeStatRepository.findById(feedId).get().getLikeCount();
        }
        return cacheSize;
    }

    public void delete(int feedId, int userPk) {
        // Redis에 좋아요 정보가 존재하는 경우
        LikeSyncData likeSyncData = (LikeSyncData) redisTemplate.opsForHash().get(LIKE_HASH_KEY, getHashKey(feedId, userPk));
        if(likeSyncData != null && likeSyncData.isStatus()) {
            redisTemplate.opsForHash().delete(LIKE_HASH_KEY, getHashKey(feedId, userPk));
            return;
        }

        // DB에서 상태 업데이트
        List<LikeSyncData> list = likeRepository.findByUserPkAndFeedId(userPk, feedId);
        if (list.size() > 0 && list.get(0).isStatus()) {
            list.get(0).setStatus(false);
            likeRepository.save(list.get(0));
            // 감소시키기
            if (likeStatRepository.findById(feedId).isPresent()) {
                likeStatRepository.findById(feedId).get().decrement();
            }
        }
    }

        public void update (LikeSyncData updatedLikeSyncData){
            // Redis에서 업데이트
            redisTemplate.opsForHash().put(LIKE_HASH_KEY, getHashKey(updatedLikeSyncData), updatedLikeSyncData);
        }

        private void saveToMySQL () {

            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

            hashOperations.scan(LIKE_HASH_KEY, ScanOptions.scanOptions().match("*").build())    // 현재 MySQL로 저장하지 않는 캐시값
                    .forEachRemaining(entry -> {
                        LikeSyncData likeSyncData = (LikeSyncData) entry.getValue();
                            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                                    LikeSyncData elem = ((List<LikeSyncData>)likeRepository.findByUserPkAndFeedId(likeSyncData.getUserPk(), likeSyncData.getFeedId())).get(0);
                                    if(likeSyncData.isStatus() == elem.isStatus()) {    // 상태가 같다면 아무 동작을 수행하지 않는다
                                        ;
                                    } else {
                                        likeRepository.save(likeSyncData);  // 상태가 다르면 저장하고
                                        if(likeSyncData.isStatus()) {   // 집계 테이블 업데이트
                                            likeStatRepository.findById(likeSyncData.getFeedId()).ifPresent(LikeStat::increment);
                                        } else {
                                            likeStatRepository.findById(likeSyncData.getFeedId()).ifPresent(LikeStat::decrement);
                                        }
                                    }
                                    // 저장 후 해당 데이터를 해시에서 삭제
                                    hashOperations.delete(LIKE_HASH_KEY, entry.getKey());
                            });

                            future.join();
                    });
        }

        private String getHashKey (LikeSyncData likeSyncData){
            return getHashKey(likeSyncData.getFeedId(), likeSyncData.getUserPk());
        }

        private String getHashKey(int feedId, int userPk){
            return feedId + "_" + userPk;
        }
    }
