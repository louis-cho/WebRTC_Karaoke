package com.ssafy.server.hit.service;

import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.syncdata.HitSyncData;

public interface HitService {

    Hit getHitById(int hitId);

    Hit createHit(Hit hit);

    void syncToDB(Integer hitId, HitSyncData likeSyncData);
}
