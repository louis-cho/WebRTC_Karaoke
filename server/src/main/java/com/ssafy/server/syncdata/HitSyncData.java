package com.ssafy.server.syncdata;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HitSyncData implements Syncable {
    private Integer hitId;
    private Integer feedId;
    private Integer userPk;
    private boolean syncedToDB = false;

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
