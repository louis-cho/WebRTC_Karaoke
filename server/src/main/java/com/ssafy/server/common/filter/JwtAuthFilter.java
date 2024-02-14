package com.ssafy.server.common.filter;

import com.ssafy.server.auth.model.dto.JwtCode;
import com.ssafy.server.auth.model.dto.Role;
import com.ssafy.server.auth.model.dto.Token;
import com.ssafy.server.auth.model.dto.TokenKey;
import com.ssafy.server.auth.util.JwtUtil;
import com.ssafy.server.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        System.out.println("JWT Filter");
        if(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()) == null) {
            // Header에 token이 없는 경우, 로그인 필요. authStatus = 0
            chain.doFilter(request, response);
            return ;
        }
        String accessToken = jwtUtil.resolveToken(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()));
        if (accessToken != null && jwtUtil.validateToken(accessToken) == JwtCode.ACCESS) {
            System.out.println("authStatus = 1");
            // accessToken이 유효한 경우. authStatus = 1
            int userPk = jwtUtil.getUserPk(accessToken);
            User user = User.builder()
                    .userPk(userPk)
                    .build();
            Authentication auth = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("set Authentication to security context for '{}', uri = {}", auth.getName(), ((HttpServletRequest)request).getRequestURI());
        } else if (accessToken != null && jwtUtil.validateToken(accessToken) == JwtCode.EXPIRED) {
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
                    System.out.println("authStatus = 2");
                    String newAccessToken = jwtUtil.generateAccess(user.getUserPk(), Role.USER.getKey());
                    // db에 있는 refreshToken과 같음, accessToken 재발급
                    Authentication auth = getAuthentication(user);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("set Authentication to security context for '{}', uri = {}", auth.getName(), ((HttpServletRequest) request).getRequestURI());
                } else {
                    System.out.println("authStatus = 3");
                    // header에 있는 refreshToken은 살아있지만, db의 refresh와 다른 경우 --> 재로그인
                    ((HttpServletResponse) response).setHeader("authStatus", "3");
                }
            } else {    // refresh도 만료된 경우
                System.out.println("authStatus = 4");
                ((HttpServletResponse) response).setHeader("authStatus", "4");
            }
        }
        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "",
                Arrays.asList(new SimpleGrantedAuthority(Role.USER.getKey())));
    }
}
