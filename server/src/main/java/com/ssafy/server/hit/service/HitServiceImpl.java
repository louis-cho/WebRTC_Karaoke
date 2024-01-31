package com.ssafy.server.hit.service;

import com.ssafy.server.common.util.LITERAL;
import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.hit.repository.HitRedisRepository;
import com.ssafy.server.hit.repository.HitRepository;
import com.ssafy.server.like.model.Likes;
import com.ssafy.server.syncdata.HitSyncData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class HitServiceImpl implements HitService {

    @Autowired
    private final HitRepository hitRepository;

    @Autowired
    private final HitRedisRepository hitRedisRepository;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    public HitServiceImpl(HitRepository hitRepository, HitRedisRepository hitRedisRepository, RedisTemplate<String, Object> redisTemplate) {
        this.hitRepository = hitRepository;
        this.hitRedisRepository = hitRedisRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Hit getHitById(int hitId) {
        return null;
    }

    @Override
    public Hit createHit(Hit hit) {
        HitSyncData hitSyncData = new HitSyncData();
        hitSyncData.setHitId(hit.getHitId());
        hitSyncData.setUserPk(hit.getUserPk());
        hitSyncData.setFeedId(hit.getFeedId());

        if(hitRedisRepository.findHitByFeedId(hit.getFeedId()) == null) {
            // DB에서 feedId 게시글의 모든 조회수 정보 가져와서 redis에 넣기
            List<Hit> hitList = hitRepository.findByFeedId(hit.getFeedId());
            int feedId = hit.getFeedId();
            for(Hit _hit : hitList) {
                HitSyncData _hitSyncData = new HitSyncData();
                _hitSyncData.setSyncedToDB(true);
                _hitSyncData.setFeedId(feedId);
                _hitSyncData.setUserPk(_hit.getUserPk());
                _hitSyncData.setHitId(_hit.getHitId());
                
                hitRedisRepository.saveHit(_hitSyncData);
            }
        }
        hitRedisRepository.saveHit(hitSyncData);
        return null;
    }

    /**
     *
     * @param hitId
     * @param hitSyncData
     */
    @Override
    public void syncToDB(Integer hitId, HitSyncData hitSyncData) {
        Hit hit = new Hit();
        hit.setHitId(hitSyncData.getHitId());
        hit.setFeedId(hitSyncData.getFeedId());
        hit.setUserPk(hitSyncData.getUserPk());

        Hit fetched = hitRepository.findById(hitId).orElse(null);
        if(fetched == null)
            hitRepository.save(hit);
    }
}
