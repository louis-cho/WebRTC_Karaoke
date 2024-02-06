package com.ssafy.server.like.repository;

import com.ssafy.server.like.model.LikeStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeStatRepository extends JpaRepository<LikeStat, Integer> {

}
