package com.example.ktb.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//  private final JwtTokenProvider jwtTokenProvider;
//  private final RedisTemplate<String, String> redisTemplate;
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request,
//      HttpServletResponse response,
//      FilterChain filterChain) throws ServletException, IOException {
//
//    String authHeader = request.getHeader("Authorization");
//
//    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//      String token = authHeader.substring(7);
//
//      // 블랙리스트(로그아웃) 토큰 체크
//      String isLogout = redisTemplate.opsForValue().get(token);
//      if (isLogout != null) {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        return;
//      }
//
//      try {
//        Claims claims = jwtTokenProvider.getClaims(token);
//        String userId = claims.getSubject();
//
//        System.out.println("JwtAuthFilter's userId: " + userId);
//
//        request.setAttribute("userId", userId);
//
//        List<GrantedAuthority> authorities = List.of(
//            new SimpleGrantedAuthority("ROLE_USER"));
//        UsernamePasswordAuthenticationToken authentication =
//            new UsernamePasswordAuthenticationToken(userId, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//      } catch (Exception e) {
//        e.printStackTrace();
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        return;
//      }
//    }
//
//    // 다음 필터로 넘김
//    filterChain.doFilter(request, response);
//  }
//}
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate<String, String> redisTemplate;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader("Authorization");

    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);

      // 블랙리스트(로그아웃) 토큰 체크
      String isLogout = redisTemplate.opsForValue().get(token);
      if (isLogout != null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      try {
        Claims claims = jwtTokenProvider.getClaims(token);
        String userIdStr = claims.getSubject();
        if (userIdStr == null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
        Long userId = Long.parseLong(userIdStr);
        request.setAttribute("userId", userId);
        
        // Spring Security context 설정 (optional)
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userId, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (Exception e) {
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }
}
