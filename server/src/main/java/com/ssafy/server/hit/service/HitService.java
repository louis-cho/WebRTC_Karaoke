package com.ssafy.server.hit.service;

import java.util.concurrent.CompletableFuture;

public interface HitService {
    void save(int feedId, int userPk);
    int findAllByFeedId(int feedId);
    CompletableFuture<Void> saveToMySQLAsync();
}
