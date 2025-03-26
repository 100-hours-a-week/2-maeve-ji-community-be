package com.example.ktb.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final String SECRET = "my_super_secret_jwt_key_for_signing_1234567890";
  private final Key secretKey = Keys.hmacShaKeyFor(SECRET.getBytes());
  private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24시간

  public String createToken(Long userId, String email) {
    return Jwts.builder()
//        .claim("userId", userId)
        .setSubject(String.valueOf(userId))
        .claim("email", email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(secretKey)
        .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
