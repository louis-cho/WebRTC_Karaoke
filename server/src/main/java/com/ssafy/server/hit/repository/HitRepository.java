package com.ssafy.server.hit.repository;

import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.like.model.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Integer> {

    List<Hit> findByFeedId(int feedId);

    List<Hit> findByUserPkAndFeedId(int userPk, int feedId);

}
