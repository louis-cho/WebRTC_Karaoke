package com.ssafy.server.comment.service;

import com.ssafy.server.comment.model.Comment;

import java.util.List;

public interface CommentService {

    void createComment(Comment newComment);

    Comment getCommentById(int commentId);

    Comment updateComment(int commentId, Comment updatedComment);

    boolean deleteComment(int commentId);

    List<Comment> getCommentsByFeedIdWithPagination(int feedId, int startIndex, int pageSize);

    int getCommentCount(int feedId);
}
