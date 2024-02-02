package com.ssafy.server.like.repository;

import com.ssafy.server.like.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Likes, Integer> {
}
