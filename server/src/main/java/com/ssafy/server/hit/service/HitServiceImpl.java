package com.ssafy.server.hit.service;

import com.ssafy.server.hit.model.Hit;
import com.ssafy.server.hit.repository.HitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HitServiceImpl implements HitService {

    @Autowired
    private final HitRepository hitRepository;

    public HitServiceImpl(HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }

    @Override
    public Hit getHitById(int hitId) {
        return null;
    }

    @Override
    public Hit createHit(Hit hit) {
        return null;
    }
}
