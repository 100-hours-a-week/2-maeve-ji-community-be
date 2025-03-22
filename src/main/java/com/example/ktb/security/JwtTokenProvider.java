package com.example.ktb.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    private final String SECRET_KEY = "eW91cl9zZWNyZXRfa2V5eW91cl9zZWNyZXRfa2V5eW91cl9zZWNyZXRfa2V5eW91cl9zZWNyZXRfa2V5eW91cl9zZWNyZXRfa2V5eW91cl9zZWNyZXRfa2V5eW91cl9zZWNyZXRfa2V5";

    // JWT 생성
    public String createToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24시간
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
