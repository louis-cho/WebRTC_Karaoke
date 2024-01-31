package com.ssafy.server.comment.service;

import com.ssafy.server.comment.model.Comment;

import com.ssafy.server.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private final CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public Comment createComment(Comment newComment) {
        // 데이터베이스에 새로운 댓글 생성
        return commentRepository.save(newComment);
    }

    @Override
    public Comment getCommentById(int commentId) {
        // commentId에 해당하는 댓글 조회
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        return optionalComment.orElse(null);
    }

    @Override
    public Comment updateComment(int commentId, Comment updatedComment) {
        // commentId에 해당하는 댓글 업데이트
        Optional<Comment> optionalExistingComment = commentRepository.findById(commentId);

        if (optionalExistingComment.isPresent()) {
            Comment existingComment = optionalExistingComment.get();
            existingComment.setUserPk(updatedComment.getUserPk());
            existingComment.setFeedId(updatedComment.getFeedId());
            existingComment.setContent(updatedComment.getContent());
            existingComment.setRootCommentId(updatedComment.getRootCommentId());
            existingComment.setParentCommentId(updatedComment.getParentCommentId());

            return commentRepository.save(existingComment);
        }

        return null;
    }

    @Override
    public boolean deleteComment(int commentId) {
        // commentId에 해당하는 댓글 삭제
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }
}
