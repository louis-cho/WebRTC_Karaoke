package com.ssafy.server.common.filter;

import com.ssafy.server.auth.model.dto.JwtCode;
import com.ssafy.server.auth.model.dto.Token;
import com.ssafy.server.auth.model.dto.TokenKey;
import com.ssafy.server.auth.util.JwtUtil;
import com.ssafy.server.user.model.User;
import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtUtil.resolveToken(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()));

        if (token != null && jwtUtil.validateToken(token) == JwtCode.ACCESS) {
            int userPk = jwtUtil.getUserPk(token);

            User user = User.builder()
                    .userPk(userPk)
                    .build();

            Authentication auth = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("set Authentication to security context for '{}', uri = {}", auth.getName(), ((HttpServletRequest)request).getRequestURI());
        } else if (token != null && jwtUtil.validateToken(token) == JwtCode.EXPIRED) {
            Claims claims = jwtUtil.getClaims(token);

            // 토큰에 저장된 유저정보
            User user = User.builder()
                    .userPk(Integer.parseInt(claims.getSubject()))
                    .build();

            // 헤더에 존재하는 리프레시 토큰
            String refresh = jwtUtil.resolveToken(
                    ((HttpServletRequest)request).getHeader(TokenKey.REFRESH.getKey()));

            // db에 존재하는 리프레시 토큰
            String savedRefresh = jwtUtil.getSavedRefresh(user.getUserPk());

            // refresh token을 확인해서 재발급
            if (token != null && refresh.equals(savedRefresh) && jwtUtil.validateToken(refresh) == JwtCode.ACCESS) {
                Token tokens = jwtUtil.generateToken(user.getUserPk());

                jwtUtil.setSaveRefresh(user.getUserPk(),
                        tokens.getRefreshToken(), jwtUtil.getExpiration(TokenKey.REFRESH));

                ((HttpServletResponse)response).setHeader(TokenKey.ACCESS.getKey(), "Bearer " + tokens.getAccessToken());
                ((HttpServletResponse)response).setHeader(TokenKey.REFRESH.getKey(), "Bearer " + tokens.getRefreshToken());

                Authentication auth = getAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("set Authentication to security context for '{}', uri = {}", auth.getName(), ((HttpServletRequest)request).getRequestURI());
            }
        } else {
            log.info("no valid JWT token found, uri: {}", ((HttpServletRequest)request).getRequestURI());
        }
        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "");
    }
}
