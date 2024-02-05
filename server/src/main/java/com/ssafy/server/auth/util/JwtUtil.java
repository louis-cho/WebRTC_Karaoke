package com.ssafy.server.auth.util;

import com.ssafy.server.auth.model.dto.JwtCode;
import com.ssafy.server.auth.model.dto.Token;
import com.ssafy.server.auth.model.dto.TokenKey;
import com.ssafy.server.auth.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
@Slf4j
public class JwtUtil implements InitializingBean {
    private final String secret;

    private final TokenService tokenService;
    private Key key;

    @Autowired
    public JwtUtil(
            @Value("${jwt.secret}")String secret,

            TokenService tokenService
    ) {
        this.secret = secret;

        this.tokenService = tokenService;
    }

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getSavedRefresh(int userPk) {
        return tokenService.getRefreshToken(userPk);
    }

    public void setSaveRefresh(int userPk, String tokenValue, LocalDateTime expiredAt) {
        tokenService.saveRefreshToken(userPk, tokenValue, expiredAt);
    }

    public String generateAccess(int userPk) {
        return createToken(String.valueOf(userPk), TokenKey.ACCESS);
    }

    public String generateRefresh(int userPk) {
        return createToken(String.valueOf(userPk), TokenKey.REFRESH);
    }

    public Token generateToken(int userPk) {
        String accessToken = generateAccess(userPk);
        String refreshToken = generateRefresh(userPk);

        return new Token(accessToken, refreshToken);
    }

    public String createToken(String userId, TokenKey tokenKey) {
        // access : 1 hour, refresh : 1 month
        LocalDateTime expiredAt = getExpiration(tokenKey);

        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("additionalData", "");

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(Date.from(expiredAt.toInstant(ZoneOffset.UTC)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtCode validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return JwtCode.ACCESS;
        } catch (ExpiredJwtException e) {
            // 만료된 경우에는 refresh token을 확인하기 위해
            return JwtCode.EXPIRED;
        } catch (JwtException | IllegalArgumentException  e) {
            log.info("jwtException = {}", e);
        }
        return JwtCode.DENIED;
    }

    public int getUserPk(String token) {
        return Integer.parseInt(Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody().getSubject());
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    public LocalDateTime getExpiration(TokenKey tokenKey) {
        LocalDateTime now = LocalDateTime.now();

        String delimiter = tokenKey.getKey();
        if (delimiter.equals(TokenKey.ACCESS.getKey())) {
            return now.plusHours(1);
        } else if (delimiter.equals(TokenKey.REFRESH.getKey())) {
            return now.plusMonths(1);
        } else {
            throw new RuntimeException("Invalid TokenKey");
        }
    }

    public String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer-")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
