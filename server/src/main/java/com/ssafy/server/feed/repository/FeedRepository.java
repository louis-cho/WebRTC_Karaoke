package com.ssafy.server.feed.repository;

import com.ssafy.server.feed.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Integer> {
    // 추가적인 메서드 구현 가능
    int deleteById(int id);  // To get deleted record count
}
