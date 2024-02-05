package com.ssafy.server.like.model;

import com.ssafy.server.syncdata.LikeSyncData;

public class LikeSyncDataFactory {
    public static LikeSyncData fromLike(Like like) {
        return new LikeSyncData(
                like.getLikeId(),
                like.getFeedId(),
                like.getUserPk(),
                like.isStatus(),
                like.isDeleted()
        );
    }
}
