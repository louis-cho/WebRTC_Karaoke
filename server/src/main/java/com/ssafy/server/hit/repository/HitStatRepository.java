package com.ssafy.server.hit.repository;

import com.ssafy.server.hit.model.HitStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HitStatRepository extends JpaRepository<HitStat, Integer> {
}
