package com.ssafy.server.auth.service;

import com.ssafy.server.auth.model.entity.RefreshToken;
import com.ssafy.server.auth.repository.TokenRepository;
import com.ssafy.server.user.model.User;
import com.ssafy.server.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private final TokenRepository tokenRepository;
    @Autowired
    private final UserRepository userRepository;

    public TokenServiceImpl(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String getRefreshToken(int userPk) {
        // userPk를 조건으로 최신순 정렬로 가져옴
        Optional<RefreshToken> refreshTokenOptional = tokenRepository.findTopByUser_UserPkOrderByExpiredAtDesc(userPk);

        // refreshToken이 존재하는 경우
        if (refreshTokenOptional.isPresent()) {
            RefreshToken refreshToken = refreshTokenOptional.get();
            return refreshToken.getTokenValue();
        } else {
            // refreshToken이 존재하지 않는 경우
            return null;
        }
    }

    @Override
    public void saveRefreshToken(int userPk, String tokenValue, LocalDateTime expiredAt) {
        User user = userRepository.findByUserPk(userPk);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenValue(tokenValue);
        refreshToken.setExpiredAt(expiredAt);

        // 저장
        tokenRepository.save(refreshToken);
    }
}
