package com.ssafy.server.like.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.like.repository.LikeRepository;
import com.ssafy.server.syncdata.LikeSyncData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {
    private static final String LIKE_HASH_KEY = "likes";
    @Autowired
    private final LikeRepository likeRepository;
    @Autowired
    private final RedisTemplate<String, LikeSyncData> redisTemplate;
    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, RedisTemplate<String, LikeSyncData> redisTemplate) {
        this.likeRepository = likeRepository;
        this.redisTemplate = redisTemplate;
    }
    public void save(LikeSyncData likeSyncData) {
        redisTemplate.opsForHash().put(LIKE_HASH_KEY, getHashKey(likeSyncData), likeSyncData);
    }

    public LikeSyncData findById(int feedId, int userPk) {
        return (LikeSyncData) redisTemplate.opsForHash().get(LIKE_HASH_KEY, getHashKey(feedId, userPk));
    }

    public Map<Object, Object> findAllByFeedId(int feedId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<Object, Object> result = new HashMap<>();

        hashOperations.scan(LIKE_HASH_KEY, ScanOptions.scanOptions().match(feedId + "_*").build())
                .forEachRemaining(entry -> result.put(entry.getKey(), entry.getValue()));

        return result;
    }
    public void delete(int feedId, int userPk) {
        // Redis에서 삭제
        redisTemplate.opsForHash().delete(LIKE_HASH_KEY, getHashKey(feedId, userPk));
     }

    public void update(LikeSyncData updatedLikeSyncData) {
        // Redis에서 업데이트
        redisTemplate.opsForHash().put(LIKE_HASH_KEY, getHashKey(updatedLikeSyncData), updatedLikeSyncData);
    }

    private void saveToMySQL(LikeSyncData likeSyncData) {
        CompletableFuture.runAsync(() -> likeRepository.save(likeSyncData));
    }

    private String getHashKey(LikeSyncData likeSyncData) {
        return getHashKey(likeSyncData.getFeedId(), likeSyncData.getUserPk());
    }

    private String getHashKey(int feedId, int userPk) {
        return feedId + "_" + userPk;
    }
}