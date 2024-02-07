package com.ssafy.server.song.repository;

import com.ssafy.server.song.model.entity.SongInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongInfoRepository extends JpaRepository<SongInfo, Integer> {
}
