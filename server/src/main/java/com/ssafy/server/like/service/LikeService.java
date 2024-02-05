package com.ssafy.server.like.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.server.syncdata.LikeSyncData;

import java.util.Map;

public interface LikeService {
    void save(LikeSyncData likeSyncData);
    LikeSyncData findById(int feedId, int userPk);
    Map<Object, Object> findAllByFeedId(int feedId);
    void delete(int feedId, int userPk);
    void update(LikeSyncData updatedLikeSyncData);
}
