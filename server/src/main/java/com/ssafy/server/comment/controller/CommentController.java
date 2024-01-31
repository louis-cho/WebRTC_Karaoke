package com.ssafy.server.comment.controller;

import com.ssafy.server.comment.model.Comment;
import com.ssafy.server.comment.service.CommentService;
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
    public ResponseEntity<Comment> getCommentById(@PathVariable int commentId) {
        Comment comment = commentService.getCommentById(commentId);

        if (comment != null) {
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Comment> createComment(@RequestBody Comment newComment) {
        Comment createdComment = commentService.createComment(newComment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<Comment> updateComment(@PathVariable int commentId, @RequestBody Comment updatedComment) {
        Comment updated = commentService.updateComment(commentId, updatedComment);

        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> deleteComment(@PathVariable int commentId) {
        boolean result = commentService.deleteComment(commentId);

        if (result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
