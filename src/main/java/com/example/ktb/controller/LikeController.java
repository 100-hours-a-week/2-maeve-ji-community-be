package com.example.ktb.controller;
import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class LikeController {

    private final LikeService likeService;

    // 좋아요 추가
    @PostMapping("/{postId}/likes")
    public ResponseEntity<?> like(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = Long.parseLong((String) request.getAttribute("userId"));
        likeService.likePost(userId, postId);
        log.info("User {} liked post {}", userId, postId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("like_success", Map.of("postId", postId)));
    }

    // 좋아요 취소
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<?> unlike(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = Long.parseLong((String) request.getAttribute("userId"));
        likeService.unlikePost(userId, postId);
        log.info("User {} unliked post {}", userId, postId);

        return ResponseEntity.noContent().build();
    }
}
