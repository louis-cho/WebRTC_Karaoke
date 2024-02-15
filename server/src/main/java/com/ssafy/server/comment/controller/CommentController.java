package com.ssafy.server.comment.controller;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.ssafy.server.api.ApiResponse;
import com.ssafy.server.comment.error.CommentExceptionEnum;
import com.ssafy.server.comment.model.Comment;
import com.ssafy.server.comment.model.CommentWithNickname;
import com.ssafy.server.comment.service.CommentService;
import com.ssafy.server.common.error.ApiException;
import com.ssafy.server.common.error.ApiExceptionFactory;
import com.ssafy.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final String START_INDEX = "0";
    private final String PAGE_SIZE = "10";

    @Autowired
    private final UserService userService;
    @Autowired
    private final CommentService commentService;

    @Autowired
    public CommentController(UserService userService, CommentService commentService) {
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping("/get/{commentId}")
    public ResponseEntity<ApiResponse<Comment>> getCommentById(@PathVariable int commentId) {
        Comment comment = null;
        try {
            comment = commentService.getCommentById(commentId);
            if (comment == null) {
                throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_NOT_FOUND));
            }
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_NOT_FOUND));
        }
        return new ResponseEntity<>(ApiResponse.success(comment), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/count/{feedId}")
    public ResponseEntity<ApiResponse<Integer>> getCommentCount(@PathVariable int feedId) {
        Integer count = 0;
        try {
            count = commentService.getCommentCount(feedId);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_NOT_FOUND));
        }
        return new ResponseEntity<>(ApiResponse.success(count), HttpStatus.ACCEPTED);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createComment(@RequestHeader(name="uuid") String uuid, @RequestBody Comment newComment) {

        int userPk = -1;
        try {
            userPk = userService.getUserPk(UUID.fromString(uuid));
            if(userPk < 0) {
                return null;
            }
            newComment.setUserPk(userPk);
            commentService.createComment(newComment);
        } catch(Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_CREATION_FAILED));
        }

        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.ACCEPTED);
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

    @PostMapping("/feed/{feedId}")
    public ResponseEntity<ApiResponse<List<CommentWithNickname>>> getCommentsByFeedIdWithPagination(
            @PathVariable int feedId,
            @RequestParam(defaultValue = START_INDEX) int startIndex,
            @RequestParam(defaultValue = PAGE_SIZE) int pageSize) {

        List<Comment> comments = null;
        List<CommentWithNickname> result = new ArrayList<>();

        try {
            comments = commentService.getCommentsByFeedIdWithPagination(feedId, startIndex, pageSize);
            for (Comment comment : comments) {
                String nickname = userService.getUserNickname(comment.getUserPk());
                result.add(new CommentWithNickname(comment, nickname));
            }
        } catch(Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(CommentExceptionEnum.COMMENT_FETCH_ERROR));
        }

        return new ResponseEntity<>(ApiResponse.success(result), HttpStatus.ACCEPTED);
    }

}
