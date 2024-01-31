package com.ssafy.server.cache.redis;

import com.ssafy.server.like.model.Likes;

/**
 * 각 도메인 별 redis 캐시 서비스를 제공하는 인터페이스
 */
public interface RedisService {

    void saveLike(Likes like);

    void updateLikeStatistics(Likes like);

}
