package com.example.ktb.service;

import com.example.ktb.security.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그아웃 시 블랙리스트 등록
    public void logout(String token) {
        Claims claims = jwtTokenProvider.getClaims(token);
        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();

        redisTemplate.opsForValue().set(token, "logout", expiration, TimeUnit.MILLISECONDS);
    }
}
