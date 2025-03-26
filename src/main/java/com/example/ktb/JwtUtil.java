package com.example.ktb;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final String secretKey = "YourSecretKeyForJWTSigning";  // 실제 운영에서는 yml로 분리 추천
  private final long expirationTime = 1000 * 60 * 60 * 24; // 24시간

  // ✅ JWT 생성 메서드 (Optional)
  public String generateToken(Long userId) {
    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();
  }

  // ✅ JWT에서 Claims 추출
  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secretKey)
        .parseClaimsJws(token)
        .getBody();
  }

  // ✅ JWT에서 userId(Long) 추출
  public Long extractUserId(String token) {
    Claims claims = extractAllClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  // ✅ JWT 유효성 검사
  public boolean validateToken(String token) {
    try {
      Claims claims = extractAllClaims(token);
      return !claims.getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }
}
