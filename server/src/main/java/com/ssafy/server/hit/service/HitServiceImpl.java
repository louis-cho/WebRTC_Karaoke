package com.ssafy.server.hit.service;

import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.hit.model.HitStat;
import com.ssafy.server.hit.repository.HitRepository;
import com.ssafy.server.hit.repository.HitStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class HitServiceImpl implements HitService {

    private static final String HIT_HASH_KEY = "hits";
    private final HitRepository hitRepository;
    private final HitStatRepository hitStatRepository;
    private final RedisTemplate<String, Hit> redisTemplate;

    @Autowired
    public HitServiceImpl(HitRepository hitRepository, HitStatRepository hitStatRepository, RedisTemplate<String, Hit> redisTemplate) {
        this.hitRepository = hitRepository;
        this.hitStatRepository = hitStatRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(int feedId, int userPk) {
        Hit hit = new Hit();

        hit.setFeedId(feedId);
        hit.setUserPk(userPk);

        redisTemplate.opsForHash().put(HIT_HASH_KEY, getHashKey(hit), hit);
    }

    @Override
    public int findAllByFeedId(int feedId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        Map<Object, Object> result = new HashMap<>();

        hashOperations.scan(HIT_HASH_KEY, ScanOptions.scanOptions().match("*" + feedId + "_*").build())
                .forEachRemaining(entry -> {
                    result.put(entry.getKey(), entry.getValue());
                });

        int cacheSize = result.size();  // 캐시 사이즈는 무조건 안맞을 수 밖에 없음 (좋아요 취소까지 계산하도록 함)

        if (hitStatRepository.findById(feedId).isPresent()) {  // DB 집계 테이블 내역도 가져오기
            cacheSize += hitStatRepository.findById(feedId).get().getHitCount();
        }
        return cacheSize;
    }

    @Override
    public CompletableFuture<Void> saveToMySQLAsync() {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        long count = hashOperations.scan(HIT_HASH_KEY, ScanOptions.scanOptions().match("*").build()).stream().count();
        System.out.println(count);

        hashOperations.scan(HIT_HASH_KEY, ScanOptions.scanOptions().match("*").build())
                .forEachRemaining(entry -> {
                    Hit hit = (Hit) entry.getValue();

                    List<Hit> list = hitRepository.findByUserPkAndFeedId(hit.getUserPk(), hit.getFeedId());
                    if (list.size() < 1) {      // DB에 조회 정보가 없는 경우
                        hitRepository.save(hit);
                        hitStatRepository.findById(hit.getFeedId())
                                .ifPresentOrElse(
                                        existingHitStat -> {
                                            // HitStat 데이터가 이미 존재하는 경우
                                            existingHitStat.increment(); // HitStat 값을 증가시킴
                                            hitStatRepository.save(existingHitStat); // 업데이트된 HitStat 데이터를 저장
                                        },
                                        () -> {
                                            // HitStat 데이터가 존재하지 않는 경우
                                            HitStat newHitStat = new HitStat();
                                            newHitStat.setFeedId(hit.getFeedId());
                                            newHitStat.setHitCount(1); // 초기 값 설정
                                            hitStatRepository.save(newHitStat); // 새로운 HitStat 데이터를 저장
                                        }
                                );
                      }
                    
                    // 저장 후 해당 데이터를 해시에서 삭제
                    hashOperations.delete(HIT_HASH_KEY, entry.getKey());
                });

        return CompletableFuture.completedFuture(null);

    }

    private String getHashKey(Hit hit) { return getHashKey(hit.getFeedId(), hit.getUserPk());}

    private String getHashKey(int feedId, int userPk) {return feedId + "_" + userPk;}
}
