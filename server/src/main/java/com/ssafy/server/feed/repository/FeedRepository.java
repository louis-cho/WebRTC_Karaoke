package com.ssafy.server.feed.repository;

import com.ssafy.server.feed.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Integer> {
    Page<Feed> findAllByOrderByTimeDesc(Pageable pageable);
    Page<Feed> findAllByOrderByTimeAsc(Pageable pageable);

    List<Feed> findAllByUserPk(int userPk);
    default Optional<List<Feed>> findByUserPk(int userPk) {
        if (userPk > 0) {
            List<Feed> feeds = findAllByUserPk(userPk);
            return Optional.of(feeds);
        } else {
            return Optional.empty();
        }
    }

}
