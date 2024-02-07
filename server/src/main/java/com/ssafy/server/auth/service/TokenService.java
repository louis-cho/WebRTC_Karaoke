package com.ssafy.server.auth.service;

import com.ssafy.server.auth.model.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.Optional;


public interface TokenService {
    String getRefreshToken(int userPk);

    void saveRefreshToken(int userPk, String tokenValue, LocalDateTime expiredAt);
}
