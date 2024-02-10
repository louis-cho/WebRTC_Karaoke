package com.ssafy.server.comment.service;

import com.ssafy.server.comment.model.Comment;

import com.ssafy.server.comment.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {


    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public void createComment(Comment newComment) {

        commentRepository.save(newComment);
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
            existingComment.setContent(updatedComment.getContent());



            return commentRepository.save(existingComment);
        }

        return null;
    }

    @Override
    public boolean deleteComment(int commentId) {
        // commentId에 해당하는 댓글 삭제

        Optional<Comment> optionalExistingComment = commentRepository.findById(commentId);
        if(optionalExistingComment.isPresent() && optionalExistingComment.get().isDeleted()) {
            optionalExistingComment.get().setDeleted(true);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
        @Override
        public List<Comment> getCommentsByFeedIdWithPagination(int feedId, int startIndex, int pageSize) {
            return (List<Comment>) entityManager.createNamedStoredProcedureQuery("GetCommentsByFeedIdWithPagination")
                    .setParameter("feedIdParam", feedId)
                    .setParameter("startIndexParam", startIndex)
                    .setParameter("pageSizeParam", pageSize)
                    .getResultList();
        }

    @Override
    public int getCommentCount(int feedId) {

        return commentRepository.countCommentsByFeedId(feedId);
    }

}
