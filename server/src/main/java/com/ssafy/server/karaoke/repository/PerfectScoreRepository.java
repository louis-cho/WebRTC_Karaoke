package com.ssafy.server.karaoke.repository;

import com.ssafy.server.karaoke.model.PerfectScore;
import com.ssafy.server.karaoke.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfectScoreRepository extends JpaRepository<PerfectScore, Integer> {
}
