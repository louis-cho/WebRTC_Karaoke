package com.ssafy.server.feed.repository;

import com.ssafy.server.feed.entity.Feed;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Integer> {
}
