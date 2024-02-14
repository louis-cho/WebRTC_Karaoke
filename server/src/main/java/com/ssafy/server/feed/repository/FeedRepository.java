package com.ssafy.server.feed.repository;

import com.ssafy.server.feed.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Feed 엔티티 데이터 액세스 인터페이스
 */
public interface FeedRepository extends JpaRepository<Feed, Integer> {

    /**
     * 시간 역순으로 정렬된 피드를 반환한다.
     *
     * @param pageable 페이징 정보
     * @return 정렬된 feed 리스트
     */
    Page<Feed> findAllByOrderByTimeDesc(Pageable pageable);

    /**
     * 시간 순으로 정렬된 피드를 반환한다.
     * 
     * @param pageable 페이징 정보
     * @return 정렬된 feed 리스트
     */
    Page<Feed> findAllByOrderByTimeAsc(Pageable pageable);

    /**
     * 특정 유저가 작성한 모든 피드를 반환한다.
     * @param userPk 유저 키
     * @return feed 리스트
     */
    List<Feed> findByUserPk(int userPk);
}
