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

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {
    private static final String LIKE_HASH_KEY = "likes";

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, LikeSyncData> hashOperations;

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }
    @Override
    public void likeFeed(int userId, int feedId) {
        LikeSyncData like = new LikeSyncData();
        like.setUserPk(userId);
        like.setFeedId(feedId);
        like.setIsDeleted(false);
        like.setSyncedToDB(false); // 새로운 좋아요를 추가할 때는 기본적으로 DB에 sync되지 않도록 설정

        // likeRepository.save(like);
        hashOperations.put(LIKE_HASH_KEY, generateKey(userId, feedId), like);
    }

    @Override
    public void unlikeFeed(int userId, int feedId) {
        // likeRepository.deleteById(generateKey(userId, feedId));
        hashOperations.delete(LIKE_HASH_KEY, generateKey(userId, feedId));
    }

    @Override
    public Map<String, LikeSyncData> getLikesForFeed(int feedId) {
        Map<String, LikeSyncData> likes = new HashMap<>();

        Cursor<Map.Entry<String, LikeSyncData>> cursor = hashOperations.scan(LIKE_HASH_KEY , ScanOptions.scanOptions().match("*").build());

        while (cursor.hasNext()) {
            ClassLoader classLoader = LikeSyncData.class.getClassLoader();
            System.out.println("Class is loaded by: " + classLoader);

            Map.Entry<String, LikeSyncData> entry = cursor.next();
            likes.put(entry.getKey(), entry.getValue());
        }

        cursor.close();

        return likes;
    }

    @Override
    public void syncLikesToDB(int userId, int feedId) {
        LikeSyncData like = hashOperations.get(LIKE_HASH_KEY, generateKey(userId, feedId));
        if (like != null && !like.isSyncedToDB()) {
            like.setSyncedToDB(true);
            // likeRepository.save(like);
        }
    }

    private String generateKey(int userId, int feedId) {
        return userId + ":" + feedId;
    }
}