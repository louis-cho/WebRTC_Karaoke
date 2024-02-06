package com.ssafy.server.auth.repository;

import com.ssafy.server.auth.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<RefreshToken, Integer> {
}
