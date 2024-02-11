package com.ssafy.server.comment.repository;

import com.ssafy.server.comment.model.Comment;
import com.ssafy.server.feed.model.Feed;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Integer> {

    @Procedure(name = "GetCommentsByFeedIdWithPagination")
    List<Comment> getCommentsByFeedIdWithPagination(
            @Param("feedIdParam") int feedId,
            @Param("startIndexParam") int startIndex,
            @Param("pageSizeParam") int pageSize);

    List<Comment> findByFeedId(int feedId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.feedId = :feedId")
    int countCommentsByFeedId(int feedId);
}
