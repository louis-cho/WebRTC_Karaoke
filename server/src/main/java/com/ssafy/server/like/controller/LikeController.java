package com.ssafy.server.like.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssafy.server.comment.error.CommentExceptionEnum;
import com.ssafy.server.common.error.ApiException;
import com.ssafy.server.common.error.ApiExceptionFactory;
import com.ssafy.server.like.error.LikeExceptionEnum;
import com.ssafy.server.like.model.Like;
import com.ssafy.server.like.model.LikeFactory;
import com.ssafy.server.like.model.LikeSyncDataFactory;
import com.ssafy.server.like.service.LikeService;
import com.ssafy.server.syncdata.LikeSyncData;
import com.ssafy.server.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/like")
public class LikeController {

    @Autowired
    private final LikeService likeService;

    @Autowired
    private final UserService userService;

    @Autowired
    public LikeController(LikeService likeService, UserService userService) {
        this.likeService = likeService;
        this.userService = userService;
    }

    @PostMapping("/testcreate")
    public void create(@RequestBody LikeSyncData likeSyncData) {
        likeService.save(likeSyncData);
    }

    @GetMapping("/testdelete/{userPk}/{feedId}")
    public void delete(@PathVariable int userPk, @PathVariable int feedId) {
        likeService.delete(userPk, feedId);
    }

    @GetMapping("/testget/{feedId}")
    public void get(@PathVariable int feedId) {
        System.out.println(likeService.findAllByFeedId(feedId));
    }

//    @GetMapping("/testsync/{userPk}/{feedId}")
//    public void sync(@PathVariable int userPk, @PathVariable int feedId) {
//        likeService.syncLikesToDB(userPk, feedId);
//    }

//    @PostMapping("/create")
//    public ResponseEntity<?> createLike(@RequestBody JsonNode request) {
//
//        String _userUUID = request.get("userUUID") != null ? request.get("userUUID").asText() : null;
//        String _feedId = request.get("feedId") != null ? request.get("feedId").asText() : null;
//
//        Like newLike = null;
//        if (_userUUID != null && _feedId != null) {
//            newLike = new Like();
//            int userPk = userService.getUserPk(UUID.fromString(_userUUID));
//            newLike.setUserPk(userPk);
//            try {
//                newLike.setFeedId(Integer.parseInt(_feedId));
//            } catch(NumberFormatException e) {
//                return null;
//            }
//        }
//
//        try {
//            LikeSyncData likeSyncData = LikeSyncDataFactory.fromLike(newLike);
//            likeService.createLike(likeSyncData);
//        } catch(Exception e) {
//            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(LikeExceptionEnum.LIKE_CREATION_FAILED));
//        }
//        return new ResponseEntity<>(true, HttpStatus.CREATED);
//        return null;
//    }

    @PostMapping("/get")
    public ResponseEntity<Like> getLikeById(@RequestParam UUID userUUID, @RequestParam int feedId) {

        try {
            int userPk = userService.getUserPk(userUUID);
            LikeSyncData likeSyncData = null; // likeService.getLikeByuserPkAndFeedId(userPk, feedId);
            Like like = LikeFactory.fromLikeSyncData(likeSyncData);
            if (like != null) {
                return new ResponseEntity<>(like, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(LikeExceptionEnum.LIKE_CREATION_FAILED));
        }
    }

//    @PutMapping("/update")
//    public ResponseEntity<Boolean> updateLike(@RequestBody Like updatedLike) {
//        try {
//            LikeSyncData likeSyncData = LikeSyncDataFactory.fromLike(updatedLike);
//            likeService.updateLike(likeSyncData);
//        } catch (Exception e) {
//            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(LikeExceptionEnum.LIKE_CREATION_FAILED));
//        }
//        return new ResponseEntity<>(true, HttpStatus.ACCEPTED);
//    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> deleteLike(@RequestBody JsonNode request) {

        String _userUUID = request.get("userUUID") != null ? request.get("userUUID").asText() : null;
        String _feedId = request.get("feedId") != null ? request.get("feedId").asText() : null;

        Like newLike = null;
        int userPk = -1;
        int feedId = -1;
        if (_userUUID != null && _feedId != null) {
            newLike = new Like();
            userPk = userService.getUserPk(UUID.fromString(_userUUID));
            newLike.setUserPk(userPk);
            try {
                feedId = Integer.parseInt(_feedId);
            } catch(NumberFormatException e) {
                return null;
            }
        }

        try {
            boolean result = false;
            // likeService.unlikeFeed(userPk, feedId);

            if (result) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        } catch(Exception e) {
            throw new ApiException(ApiExceptionFactory.fromExceptionEnum(LikeExceptionEnum.LIKE_CREATION_FAILED));
        }
    }



}
