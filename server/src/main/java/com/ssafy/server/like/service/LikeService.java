package com.ssafy.server.like.service;

import com.ssafy.server.like.model.Likes;

public interface LikeService {
    void createLike(Likes newLike);
    Likes getLikeById(int likeId);
    Likes updateLike(Likes updatedLike);
    boolean deleteLike(int likeId);
}
