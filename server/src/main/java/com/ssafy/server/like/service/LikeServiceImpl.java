package com.ssafy.server.like.service;


import com.ssafy.server.hit.model.HitStat;
import com.ssafy.server.like.model.LikeStats;
import com.ssafy.server.like.repository.LikeRepository;
import com.ssafy.server.like.repository.LikeStatRepository;
import com.ssafy.server.like.model.Like;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LikeServiceImpl implements LikeService {
    private static final String LIKE_HASH_KEY = "likes";
    private final LikeRepository likeRepository;
    private final LikeStatRepository likeStatRepository;
    private final RedisTemplate<String, Like> redisTemplate;

    @Autowired
    public LikeServiceImpl(LikeRepository likeRepository, LikeStatRepository likeStatRepository, RedisTemplate<String, Like> redisTemplate) {
        this.likeRepository = likeRepository;
        this.likeStatRepository = likeStatRepository;
        this.redisTemplate = redisTemplate;
    }

    public void save(int feedId, int userPk) {
        Like like = new Like();

        like.setFeedId(feedId);
        like.setUserPk(userPk);
        like.setStatus(true);

        // 모든 내역은 캐시에 먼저 저장한다
        redisTemplate.opsForHash().put(LIKE_HASH_KEY, getHashKey(like), like);
    }

    public Like findById(int feedId, int userPk) {
        List<Like> list = likeRepository.findByUserPkAndFeedId(userPk, feedId); // DB 데이터 반환
        if (list.size() == 0) {  // DB에 없으면 redis에서 반환
            return (Like) redisTemplate.opsForHash().get(LIKE_HASH_KEY, getHashKey(feedId, userPk));
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
        Like like = (Like) redisTemplate.opsForHash().get(LIKE_HASH_KEY, getHashKey(feedId, userPk));
        if(like != null && like.isStatus()) {
            redisTemplate.opsForHash().delete(LIKE_HASH_KEY, getHashKey(feedId, userPk));
            return;
        }

        // DB에서 상태 업데이트
        List<Like> list = likeRepository.findByUserPkAndFeedId(userPk, feedId);
        if (list.size() > 0 && list.get(0).isStatus()) {
            list.get(0).setStatus(false);
            likeRepository.save(list.get(0));
            // 감소시키기
            if (likeStatRepository.findById(feedId).isPresent()) {
                likeStatRepository.findById(feedId).get().decrement();
            }
        }
    }

    @Override
    public boolean isClicked(int feedId, int userPk) {
        try {
            List<Like> like = likeRepository.findByUserPkAndFeedId(userPk, feedId);
            if (like.get(0).isStatus() == true) {
                return true;
            }

            Like cacheLike = (Like) redisTemplate.opsForHash().get(LIKE_HASH_KEY, getHashKey(feedId, userPk));
            if (cacheLike.isStatus() == true) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public void update (Like updatedLike){
            // Redis에서 업데이트
            redisTemplate.opsForHash().put(LIKE_HASH_KEY, getHashKey(updatedLike), updatedLike);
        }

    @Async
    public CompletableFuture<Void> saveToMySQLAsync() {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        long count = hashOperations.scan(LIKE_HASH_KEY, ScanOptions.scanOptions().match("*").build()).stream().count();
        System.out.println(count);

        hashOperations.scan(LIKE_HASH_KEY, ScanOptions.scanOptions().match("*").build())
                .forEachRemaining(entry -> {
                    Like like = (Like) entry.getValue();
                    System.out.println(like);

                    List<Like> list = likeRepository.findByUserPkAndFeedId(like.getUserPk(), like.getFeedId());
                    if (list.size() < 1) {
                        likeRepository.save(like);
                        likeStatRepository.findById(like.getFeedId())
                                .ifPresentOrElse(
                                        existingLikeStat -> {
                                            // LikeStat 데이터가 이미 존재하는 경우
                                            if(like.isStatus())
                                            {
                                                existingLikeStat.increment(); // LikeStat 값을 증가시킴
                                            }
                                            else {
                                                existingLikeStat.decrement();
                                            }
                                            likeStatRepository.save(existingLikeStat); // 업데이트된 HitStat 데이터를 저장

                                         },
                                        () -> {
                                            // LikeStat 데이터가 존재하지 않는 경우
                                            LikeStats newLikeStat = new LikeStats();
                                            newLikeStat.setFeedId(like.getFeedId());
                                            newLikeStat.setLikeCount(1); // 초기 값 설정
                                            likeStatRepository.save(newLikeStat); // 새로운 HitStat 데이터를 저장
                                        }
                                );     hashOperations.delete(LIKE_HASH_KEY, entry.getKey());
                        return;
                    }

                    Like elem = list.get(0);
                    if (like.isStatus() == elem.isStatus()) {
                        // 상태가 같다면 아무 동작을 수행하지 않는다
                        return;
                    } else {
                        likeRepository.save(like);  // 상태가 다르면 저장하고
                        if (like.isStatus()) {   // 집계 테이블 업데이트
                            likeStatRepository.findById(like.getFeedId()).ifPresent(LikeStats::increment);
                        } else {
                            likeStatRepository.findById(like.getFeedId()).ifPresent(LikeStats::decrement);
                        }
                    }

                    // 저장 후 해당 데이터를 해시에서 삭제
                    hashOperations.delete(LIKE_HASH_KEY, entry.getKey());
                    System.out.println("Exiting asynchronous task");
                });

        return CompletableFuture.completedFuture(null);
    }
        private String getHashKey (Like like){
            return getHashKey(like.getFeedId(), like.getUserPk());
        }

        private String getHashKey(int feedId, int userPk){
            return feedId + "_" + userPk;
        }
    }
