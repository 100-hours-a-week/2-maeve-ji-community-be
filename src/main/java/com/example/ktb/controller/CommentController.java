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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

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
}
