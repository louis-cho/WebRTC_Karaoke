package com.ssafy.server.comment.controller;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.ssafy.server.api.ApiResponse;
import com.ssafy.server.comment.error.CommentExceptionEnum;
import com.ssafy.server.comment.model.Comment;
import com.ssafy.server.comment.service.CommentService;
import com.ssafy.server.common.error.ApiException;
import com.ssafy.server.common.error.ApiExceptionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/get/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> getCommentById(@PathVariable int commentId) {
        Comment comment = null;
        try {
            comment = commentService.getCommentById(commentId);
            if(comment == null) {
                throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_NOT_FOUND));
            }
        } catch(Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_NOT_FOUND));
        }
        return new ResponseEntity<>(ApiResponse.success(comment), HttpStatus.ACCEPTED);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Comment>> createComment(@RequestBody Comment newComment) {

        Comment created = commentService.createComment(newComment);
        if(created == null) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_CREATION_FAILED));
        }

        return new ResponseEntity<>(ApiResponse.success(created), HttpStatus.ACCEPTED);
    }

    @PostMapping("/update/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> updateComment(@PathVariable int commentId, @RequestBody Comment updatedComment) {
        Comment updated = commentService.updateComment(commentId, updatedComment);

       if(updated == null) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_UPDATE_FAILED));
       }

       return new ResponseEntity<>(ApiResponse.success(updated), HttpStatus.ACCEPTED);
    }

    @PostMapping("/delete/{commentId}")
    public ResponseEntity<ApiResponse<Boolean>> deleteComment(@PathVariable int commentId) {
        boolean result = commentService.deleteComment(commentId);

        if(result == false) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_DELETE_FAILED));
        }

        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.ACCEPTED);
    }
}
