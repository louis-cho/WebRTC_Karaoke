package com.ssafy.server.song.repository;

import com.ssafy.server.song.model.entity.SingLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SingLogRepository extends JpaRepository<SingLog, Integer> {
}
