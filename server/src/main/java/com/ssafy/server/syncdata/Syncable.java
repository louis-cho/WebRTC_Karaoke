package com.ssafy.server.syncdata;

public interface Syncable {
    boolean isSyncedToDB();

    void setSyncedToDB(boolean syncedToDB);
}