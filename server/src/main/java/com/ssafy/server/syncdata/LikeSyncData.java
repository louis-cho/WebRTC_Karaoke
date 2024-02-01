package com.ssafy.server.syncdata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LikeSyncData implements Syncable {

    private Integer likeId;
    private Integer feedId;
    private Integer userPk;
    private Character status;
    private boolean syncedToDB;


    // Constructors, getters, and setters

    @Override
    public boolean isSyncedToDB() {
        return syncedToDB;
    }

    @Override
    public void setSyncedToDB(boolean syncedToDB) {
        this.syncedToDB = syncedToDB;
    }
}