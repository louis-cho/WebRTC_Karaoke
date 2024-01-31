package com.ssafy.server.karaoke.repository;

import com.ssafy.server.karaoke.model.SongInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongInfoRepository extends JpaRepository<SongInfo, Integer> {
}
