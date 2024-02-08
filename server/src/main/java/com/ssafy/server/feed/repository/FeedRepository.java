package com.ssafy.server.feed.repository;

import com.ssafy.server.feed.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Integer> {
    Page<Feed> findAllByOrderByTimeDesc(Pageable pageable);
    Page<Feed> findAllByOrderByTimeAsc(Pageable pageable);
}
