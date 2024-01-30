package com.ssafy.server.comment.repository;

import com.ssafy.server.comment.model.Comment;
import com.ssafy.server.feed.model.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Integer> {
}
