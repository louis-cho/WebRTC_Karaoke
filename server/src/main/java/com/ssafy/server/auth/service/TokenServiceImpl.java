package com.ssafy.server.auth.service;

import com.ssafy.server.auth.model.entity.RefreshToken;
import com.ssafy.server.auth.repository.TokenRepository;
import com.ssafy.server.user.model.User;
import com.ssafy.server.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        Optional<RefreshToken> tokenOptional = tokenRepository.findById(userPk);

        return tokenOptional.map(RefreshToken::getTokenValue).orElse(null);
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
