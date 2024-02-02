package com.ssafy.server.comment.service;

import com.ssafy.server.comment.model.Comment;

public interface CommentService {
    Comment createComment(Comment newComment);

    Comment getCommentById(int commentId);

    Comment updateComment(int commentId, Comment updatedComment);

    boolean deleteComment(int commentId);
}
