package com.ssafy.server.hit.service;

import com.ssafy.server.hit.model.Hit;

public interface HitService {

    Hit getHitById(int hitId);

    Hit createHit(Hit hit);
}
