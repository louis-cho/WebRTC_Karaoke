package com.ssafy.server.cache.redis;

import com.ssafy.server.like.model.Likes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class RedisServiceImpl implements RedisService{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String LIKE_KEY_PREFIX = "like:";
    private static final String VIEW_KEY_PREFIX = "view:";

    @Override
    public void saveLike(Likes like) {
        String key = LIKE_KEY_PREFIX + like.getFeedId();

        if(like.getStatus() == 'X') {
            redisTemplate.opsForSet().remove(key, like.getLikeId());
        } else {
            if(redisTemplate.opsForSet().isMember(key, like.getLikeId()) == false) {
                redisTemplate.opsForSet().add(key, like.getLikeId());
            }
        }
    }

    @Override
    public void updateLikeStatistics(Likes like) {
        String key = LIKE_KEY_PREFIX + like.getFeedId();

        Long likeCount = redisTemplate.opsForSet().size(key); // 좋아요 갯수 조회


        // 여기에 Elasticsearch나 다른 저장소에 좋아요 통계를 업데이트하는 로직 추가
    }
}
