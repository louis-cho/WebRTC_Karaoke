package com.ssafy.server.like.model;

import com.ssafy.server.syncdata.LikeSyncData;

public class LikeFactory {

    public static Like fromLikeSyncData(LikeSyncData likeSyncData) {
        return new Like(
                likeSyncData.getLikeId(),
                likeSyncData.getUserPk(),
                likeSyncData.isStatus(),
                likeSyncData.getIsDeleted()
        );
    }


}
