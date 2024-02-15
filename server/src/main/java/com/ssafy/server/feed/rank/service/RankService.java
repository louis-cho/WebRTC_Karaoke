package com.ssafy.server.feed.rank.service;

import com.ssafy.server.feed.rank.document.FeedStatsDocument;

import java.util.List;

/**
 * 실시간 랭킹을 제공하기 위한 인터페이스
 */
public interface RankService {
    /**
     * 상위 100개의 feed 통계를 반환한다.
     * @return 피드 통계 리스트
     */
    public List<FeedStatsDocument> getTop100Ranking();
}
