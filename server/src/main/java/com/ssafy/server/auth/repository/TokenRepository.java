package com.ssafy.server.auth.repository;

import com.ssafy.server.auth.model.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findTopByUser_UserPkOrderByExpiredAtDesc(int userPk);

    List<RefreshToken> findByUser_UserPkOrderByExpiredAtDesc(int userPk);

    List<RefreshToken> findByUser_UserPk(int userPk);
}
