package com.ssafy.server.feed.rank.controller;

import com.ssafy.server.comment.model.Comment;
import com.ssafy.server.comment.service.CommentService;
import com.ssafy.server.feed.rank.model.FeedStats;
import com.ssafy.server.like.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.PriorityQueue;

@RestController
@RequestMapping("/rank")
public class RankController {

    private final CommentService commentService;
    private final LikeService likeService;

    @Autowired
    public RankController(CommentService commentService, LikeService likeService) {
        this.commentService = commentService;
        this.likeService = likeService;
    }
    @PostMapping("/getList")
    public ResponseEntity<PriorityQueue<FeedStats>> getRanking() {
        // 가장 간단한 형태로 좋아요와 댓글 수를 합산하여 랭킹을 구한다.
        PriorityQueue<FeedStats> ranking = new PriorityQueue<>();


        return new ResponseEntity<>(ranking, HttpStatus.OK);
    }

}
