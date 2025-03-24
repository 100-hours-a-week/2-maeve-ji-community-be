package com.example.ktb.controller;

import com.example.ktb.dto.CommentDto;
import com.example.ktb.entity.Comment;
import com.example.ktb.service.CommentService;
import com.example.ktb.service.PostService;
import com.example.ktb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    // 댓글 목록 조회

    // 댓글 등록
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @RequestBody CommentDto commentDto,
                                           HttpServletRequest request) {

        try {
            Long userId = Long.parseLong((String) request.getAttribute("userId"));  // jwt에서 가져옴?
            log.info(">> Controller: userId={}", userId);
            log.info(">> Controller: postId={}", postId);
            Long commentId = commentService.createComment(commentDto, postId, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "comment_success", "data", Map.of("comment_id", commentId)));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "comment_not_found", "data", null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "comment_forbidden", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "comment_bad_request", "data", null));
        }
    }

    // 댓글 수정
    @PatchMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @RequestBody Map<String, String> body,
                                           HttpServletRequest request) {
        try {
            Long userId = Long.parseLong((String) request.getAttribute("userId"));
            String content = body.get("comment");

            log.info(">> Controller: userId={}", userId);
            log.info(">> Controller: commentId={}", commentId);
            log.info(">> Controller: comment={}", content);
            commentService.updateComment(postId, commentId, userId, content);
            return ResponseEntity.noContent().build();  // 204 no content 성공
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "comment_not_found", "data", null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "comment_forbidden", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "comment_bad_request", "data", null));
        }
    }

    // 댓글 삭제



}
