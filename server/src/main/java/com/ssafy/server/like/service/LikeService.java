package com.ssafy.server.like.service;

import java.util.concurrent.CompletableFuture;

public interface LikeService {
    void save(int feedId, int userPk);
    int findAllByFeedId(int feedId);
    void delete(int feedId, int userPk);

    boolean isClicked(int feedId, int userPk);
     CompletableFuture<Void> saveToMySQLAsync();
}
