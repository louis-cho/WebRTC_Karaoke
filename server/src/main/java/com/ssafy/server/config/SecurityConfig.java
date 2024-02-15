package com.ssafy.server.config;

import com.ssafy.server.auth.util.JwtUtil;
import com.ssafy.server.common.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final JwtUtil jwtUtil;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .antMatchers(
                        "/favicon.ico",
                        "/error",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/webjars/**"
                )
                .and()
                .ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());    // 정적인 리소스들에 대해서 시큐리티 적용 무시.
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // cors 설정
        http.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                // REST 방식 사용 -> csrf, form로그인, 세션 무시
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // token 검증하는 페이지&메인페이지는 인가 허가, 외엔 모두 인가 필요
                .authorizeRequests()
                .antMatchers("/api/v1/auth/filter").permitAll()
                .antMatchers("/api/v1/user/login").permitAll()
                .antMatchers("/api/v1/user/register").permitAll()
                .antMatchers("/api/ws/**").permitAll()
                .antMatchers("/api/v1/user/get/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .logout().logoutSuccessUrl("/");

         http.addFilterBefore(new JwtAuthFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
