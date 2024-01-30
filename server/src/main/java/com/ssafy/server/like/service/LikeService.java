package com.ssafy.server.like.service;

import com.ssafy.server.like.model.Like;

import java.util.List;

public interface LikeService {
    Like createLike(Like newLike);
    Like getLikeById(int likeId);
    Like updateLike(int likeId, Like updatedLike);
    boolean deleteLike(int likeId);
}
