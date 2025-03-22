package com.example.ktb.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // CSRF 비활성화 (POST 테스트용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/sessions", "/users/**").permitAll()  // 회원가입, 로그인 열어두기
                        .anyRequest().permitAll()  // 나머지 요청도 허용 (개발 단계라면)
                )
                .httpBasic(httpBasic -> httpBasic.disable())  // 기본 로그인 창 제거
                .formLogin(form -> form.disable());  // form 로그인도 제거

        return http.build();
    }
}
