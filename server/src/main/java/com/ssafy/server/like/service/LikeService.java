package com.ssafy.server.like.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.server.syncdata.LikeSyncData;

import java.util.Map;

public interface LikeService {
    void save(int feedId, int userPk);
    int findAllByFeedId(int feedId);
    void delete(int feedId, int userPk);
}
