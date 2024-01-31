package com.ssafy.server.hit.repository;

import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.syncdata.HitSyncData;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
public class HitRedisRepository {

    private final String HASH_KEY = "hits"; // Redis의 Hash 키

    private final RedisTemplate<String, Set<HitSyncData>> redisTemplate;
    private final HashOperations<String, Integer, Set<HitSyncData>> hashOperations;

    public HitRedisRepository(RedisTemplate<String, Set<HitSyncData>> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    public void saveHit(HitSyncData hit) {
        if(hit == null)
            return;

        int feedId = hit.getFeedId();
        
        if (hashOperations.hasKey(HASH_KEY, feedId)) {  // 이미 feed Id에 해당하는 데이터가 존재하는 경우
            Set<HitSyncData> existingHits = hashOperations.get(HASH_KEY, feedId);
            existingHits.add(hit);
            hashOperations.put(HASH_KEY, feedId, existingHits);
        } else {    // feed Id 에 해당하는 조회수가 없는 경우
            Set<HitSyncData> newHits = new HashSet<>();
            newHits.add(hit);
            hashOperations.put(HASH_KEY, feedId, newHits);
        }
    }

    public Map<Integer, Set<HitSyncData>> findAllHits() {
        return hashOperations.entries(HASH_KEY);
    }

    public Set<HitSyncData> findHitByFeedId(int feedId) {
        return hashOperations.get(HASH_KEY, feedId);
    }

    public void deleteHitByFeedId(int feedId) {
        hashOperations.delete(HASH_KEY, feedId);
    }
}