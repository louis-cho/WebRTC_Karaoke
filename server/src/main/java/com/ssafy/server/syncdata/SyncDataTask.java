package com.ssafy.server.syncdata;

import co.elastic.clients.elasticsearch.transform.Sync;
import com.ssafy.server.common.util.LITERAL;
import com.ssafy.server.hit.repository.HitRedisRepository;
import com.ssafy.server.hit.service.HitService;
import com.ssafy.server.like.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Redis에서 MySQL DB로 데이터를 옮기는 작업
 */
@Component
public class SyncDataTask {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private HitRedisRepository hitRedisRepository;

    @Autowired
    private LikeService likeService;

    @Autowired
    private HitService hitService;




    @Scheduled(fixedDelay = 10000)
    public void syncDataToDB() {
        syncLikesDataToDB();
        syncHitsDataToDB();
    }

    private void syncLikesDataToDB() {
        HashOperations<String, Integer, LikeSyncData> hashOperations = redisTemplate.opsForHash();

        // Assuming your hash contains user_id as the key and like_count as the value
        Map<Integer, LikeSyncData> likesData = hashOperations.entries(LITERAL.LIKES_HASH_KEY);

        for (Map.Entry<Integer, LikeSyncData> entry : likesData.entrySet()) {

            Integer likeId = entry.getKey();
            LikeSyncData likeSyncData = entry.getValue();

            if(likeSyncData.isSyncedToDB()) {
                continue;
            }

            // Call your service method to sync data to the database
            likeService.syncToDB(likeId, likeSyncData);

            likeSyncData.setSyncedToDB(true);
        }
    }

    private void syncHitsDataToDB() {
        for (Map.Entry<Integer, Set<HitSyncData>> entry : hitRedisRepository.findAllHits().entrySet()) {
            Set<HitSyncData> hitSyncDataSet = entry.getValue();

            for (HitSyncData hitSyncData : hitSyncDataSet) {
                if (hitSyncData.isSyncedToDB()) {
                    continue;
                }

                // Call your service method to sync data to the database
                hitService.syncToDB(hitSyncData.getFeedId(), hitSyncData);

                hitSyncData.setSyncedToDB(true);
            }
        }
    }
}
