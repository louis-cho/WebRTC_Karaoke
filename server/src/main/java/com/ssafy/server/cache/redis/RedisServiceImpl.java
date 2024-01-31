package com.ssafy.server.cache.redis;

import com.ssafy.server.like.model.Likes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceImpl implements RedisService{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_KEY_PREFIX = "like:";
    private static final String VIEW_KEY_PREFIX = "view:";

    @Override
    public void saveLike(Likes like) {
        String key = LIKE_KEY_PREFIX + like.getFeedId();
        redisTemplate.opsForSet().add(key, like.getUserPk());
    }

    @Override
    public void updateLikeStatistics(Likes like) {
        String key = LIKE_KEY_PREFIX + like.getFeedId();
        Long likeCount = redisTemplate.opsForSet().size(key); // 좋아요 갯수 조회


        // 여기에 Elasticsearch나 다른 저장소에 좋아요 통계를 업데이트하는 로직 추가
    }
}
