package com.ssafy.server.like.repository;

import com.ssafy.server.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    List<Like> findByUserPkAndFeedId(int userPk, int feedId);
    List<Like> findByFeedId(int feedId);
}
