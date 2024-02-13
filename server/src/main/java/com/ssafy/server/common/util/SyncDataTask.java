package com.ssafy.server.common.util;

import com.ssafy.server.hit.service.HitService;
import com.ssafy.server.like.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Redis에서 MySQL DB로 데이터를 옮기는 작업
 */
@Component
public class SyncDataTask {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HitService hitService;

    @Scheduled(fixedRate = 15 * 60 * 1000) // 15분마다 실행
    public void syncDataToDB() {
        syncLikesDataToDB();
        syncHitsDataToDB();
    }

    public void syncLikesDataToDB() {
        CompletableFuture<Void> future = likeService.saveToMySQLAsync();

        // 비동기 작업이 완료될 때까지 대기
        future.join();
    }

    private void syncHitsDataToDB() {
       CompletableFuture<Void> future = hitService.saveToMySQLAsync();
       
       // 비동기 작업이 완료될 때까지 대기
       future.join();
    }
}
