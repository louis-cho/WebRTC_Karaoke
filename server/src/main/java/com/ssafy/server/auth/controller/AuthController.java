package com.ssafy.server.auth.controller;


import com.ssafy.server.auth.model.dto.JwtCode;
import com.ssafy.server.auth.model.dto.Role;
import com.ssafy.server.auth.model.dto.TokenKey;
import com.ssafy.server.auth.util.JwtUtil;
import com.ssafy.server.user.model.User;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/filter")
    public ResponseEntity<HashMap<String, String>> loginCheck(ServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        if(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()) == null) {
            // header에 token이 없는 경우, authStatus = 0
            result.put("authStatus", "0");
            return ResponseEntity.ok(result);
        }

        String accessToken = jwtUtil.resolveToken(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()));
        if (accessToken != null && jwtUtil.validateToken(accessToken) == JwtCode.ACCESS) {
            // accessToken이 유효한 경우. authStatus = 1
            result.put("authStatus", "1");
            return ResponseEntity.ok(result);
        }

        if (accessToken != null && jwtUtil.validateToken(accessToken) == JwtCode.EXPIRED) {
            String refreshToken = jwtUtil.resolveToken(((HttpServletRequest)request).getHeader(TokenKey.REFRESH.getKey()));
            // accessToken 만료된 경우, refreshToken 확인
            if(refreshToken != null && jwtUtil.validateToken(refreshToken) == JwtCode.ACCESS) {
                // Header에 있는 refresh가 아직 만료되지 않은 경우.
                Claims claims = jwtUtil.getClaims(refreshToken);
                // 토큰에 저장된 유저정보
                User user = User.builder()
                            .userPk(Integer.parseInt(claims.getSubject()))
                            .build();
                // db에 존재하는 리프레시 토큰
                String savedRefresh = jwtUtil.getSavedRefresh(user.getUserPk());
                if(refreshToken.equals(savedRefresh)) {
                    // db에 있는 refreshToken과 header에 있는 refreshToken 비교.
                    String newAccessToken = jwtUtil.generateAccess(user.getUserPk(), Role.USER.getKey());
                    // db에 있는 refreshToken과 같음, AccessToken 재발급
                    result.put(TokenKey.ACCESS.getKey(), "Bearer-" + newAccessToken);
                    result.put(TokenKey.REFRESH.getKey(), "Bearer-" + refreshToken);
                    result.put("authStatus", "2");
                    return ResponseEntity.ok(result);
                } else {
                    // header에 있는 refreshToken은 살아있지만, db의 refresh와 다른 경우 --> 재로그인
                    // db의 refreshToken과 다름, authStatus = 3
                    result.put("authStatus", "3");
                    return ResponseEntity.ok(result);
                }
            } else {    // refresh도 만료된 경우
                result.put("authStatus", "4");
                return ResponseEntity.ok(result);
            }
        }

        return null;
    }

}
