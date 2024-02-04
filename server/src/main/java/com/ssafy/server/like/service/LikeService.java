package com.ssafy.server.like.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ssafy.server.syncdata.LikeSyncData;

import java.util.Map;

public interface LikeService {







    void likeFeed(int userId, int feedId);
    void unlikeFeed(int userId, int feedId);
    Map<String, LikeSyncData> getLikesForFeed(int feedId);
    void syncLikesToDB(int userId, int feedId);


}
