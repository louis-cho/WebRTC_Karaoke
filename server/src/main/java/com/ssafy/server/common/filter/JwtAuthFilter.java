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
        if(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()) == null) {
            // header에 token이 없는 경우, 그냥 돌아가
            log.info("no valid JWT token found, uri: {}", ((HttpServletRequest)request).getRequestURI());
            System.out.println("Header에 token이 없는 경우, 로그인 필요. authStatus = 0");
            ((HttpServletResponse) response).setHeader("authStatus", "0");
            chain.doFilter(request, response);
            return ;
        }
        String accessToken = jwtUtil.resolveToken(((HttpServletRequest)request).getHeader(TokenKey.ACCESS.getKey()));
        log.info("Start of Filter");
        if (accessToken != null && jwtUtil.validateToken(accessToken) == JwtCode.ACCESS) {
            System.out.println("AccessToken이 유효한 경우. authStatus = 1");
            ((HttpServletResponse) response).setHeader("authStatus", "1");
            int userPk = jwtUtil.getUserPk(accessToken);
            System.out.println("userPk : " + " " + userPk);

            User user = User.builder()
                    .userPk(userPk)
                    .build();

            Authentication auth = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("set Authentication to security context for '{}', uri = {}", auth.getName(), ((HttpServletRequest)request).getRequestURI());
        } else if (accessToken != null && jwtUtil.validateToken(accessToken) == JwtCode.EXPIRED) {
            String refreshToken = jwtUtil.resolveToken(((HttpServletRequest)request).getHeader(TokenKey.REFRESH.getKey()));
            System.out.println("accessToken 만료된 경우, refreshToken 확인: " + " " + refreshToken);

            if(refreshToken != null && jwtUtil.validateToken(refreshToken) == JwtCode.ACCESS) {
                // Header에 있는 refresh가 아직 만료되지 않은 경우.
                System.out.println("access는 만료, refresh는 살아있음");
                Claims claims = jwtUtil.getClaims(refreshToken);
                System.out.println("userPk: " + " " + jwtUtil.getUserPk(refreshToken));
                // 토큰에 저장된 유저정보
                User user = User.builder()
                        .userPk(Integer.parseInt(claims.getSubject()))
                        .build();
                System.out.println("userPk: " + " " + user.getUserPk());
                // db에 존재하는 리프레시 토큰
                String savedRefresh = jwtUtil.getSavedRefresh(user.getUserPk());

                System.out.println("savedRefresh: " + " " + savedRefresh);
                System.out.println("refreshToken: " + " " + refreshToken);

                if(refreshToken.equals(savedRefresh)) {
                    // db에 있는 refreshToken과 header에 있는 refreshToken 비교.
                    String newAccessToken = jwtUtil.generateAccess(user.getUserPk(), Role.USER.getKey());
                    System.out.println("db에 있는 refreshToken과 같음, AccessToken 재발급: " + " " + accessToken);
                    System.out.println("authStatus = 2");

                    ((HttpServletResponse) response).setHeader(TokenKey.ACCESS.getKey(), "Bearer-" + newAccessToken);
                    ((HttpServletResponse) response).setHeader(TokenKey.REFRESH.getKey(), "Bearer-" + refreshToken);
                    ((HttpServletResponse) response).setHeader("authStatus", "2");

                    Authentication auth = getAuthentication(user);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.info("set Authentication to security context for '{}', uri = {}", auth.getName(), ((HttpServletRequest) request).getRequestURI());
                } else {
                    // header에 있는 refreshToken은 살아있지만, db의 refresh와 다른 경우 --> 재로그인
                    System.out.println("db의 refresh token과 다름, authStatus = 3");
                    ((HttpServletResponse) response).setHeader("authStatus", "3");
                }
            } else {    // refresh도 만료된 경우
                System.out.println("refresh 토큰도 만료 authStatus = 4");
                ((HttpServletResponse) response).setHeader("authStatus", "4");
            }
        }

        System.out.println("End of Filter");
        chain.doFilter(request, response);
    }

    public Authentication getAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user, "",
                Arrays.asList(new SimpleGrantedAuthority(Role.USER.getKey())));
    }
}
