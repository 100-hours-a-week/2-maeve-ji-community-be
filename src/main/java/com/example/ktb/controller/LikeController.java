package com.example.ktb.controller;

import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class LikeController {

  private final LikeService likeService;

  // 좋아요 추가
  @PostMapping("/{postId}/likes")
  public ResponseEntity<?> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    boolean liked = likeService.toggleLike(userId, postId);

    log.info("User {} {} post {}", userId, liked ? "liked" : "unliked", postId);

    return liked
        ? ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("like_success", Map.of("postId", postId)))
        : ResponseEntity.status(HttpStatus.OK)
            .body(new ApiResponse("unlike_success", Map.of("postId", postId)));
  }

//  @PostMapping("/{postId}/likes")
//  public ResponseEntity<?> like(@PathVariable Long postId, HttpServletRequest request) {
//    Long userId = (Long) request.getAttribute("userId");
//    likeService.likePost(userId, postId);
//    log.info("User {} liked post {}", userId, postId);
//
//    return ResponseEntity.status(HttpStatus.CREATED)
//        .body(new ApiResponse("like_success", Map.of("postId", postId)));
//  }

  // 좋아요 취소
  @DeleteMapping("/{postId}/likes")
  public ResponseEntity<?> unlike(@PathVariable Long postId, HttpServletRequest request) {
    Long userId = (Long) request.getAttribute("userId");
    likeService.unlikePost(userId, postId);
    log.info("User {} unliked post {}", userId, postId);

    return ResponseEntity.noContent().build();
  }
}
