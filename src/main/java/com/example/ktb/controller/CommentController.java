package com.example.ktb.controller;

import com.example.ktb.dto.CommentDto;
import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.service.CommentService;
import com.example.ktb.service.PostService;
import com.example.ktb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
      log.info(">> Controller: postId={}", postId);
      log.info(">> Controller: userId={}", request.getAttribute("userId"));
      log.info(">> Controller: comment body={}", commentDto);

      Long userId = (Long) request.getAttribute("userId");  // jwt에서 가져옴?
      Long commentId = commentService.createComment(commentDto, postId, userId);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("like_success", Map.of("comment_id", commentId)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body(new ApiResponse("comment_not_found", null));
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(403).body(new ApiResponse("comment_forbidden", null));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new ApiResponse("comment_bad_request", null));
    }
  }

  // 댓글 수정
  @PatchMapping("/posts/{postId}/comments/{commentId}")
  public ResponseEntity<?> updateComment(@PathVariable Long postId,
      @PathVariable Long commentId,
      @RequestBody Map<String, String> body,
      HttpServletRequest request) {
    try {
      Long userId = (Long) request.getAttribute("userId");
      String content = body.get("comment");
      
      log.info(">> Controller: userId={}", userId);
      log.info(">> Controller: commentId={}", commentId);
      log.info(">> Controller: comment={}", content);
      commentService.updateComment(postId, commentId, userId, content);
      return ResponseEntity.noContent().build();  // 204 no content 성공
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body(new ApiResponse("comment_not_found", null));
    } catch (SecurityException e) {
      return ResponseEntity.status(403).body(new ApiResponse("comment_forbidden", null));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new ApiResponse("comment_bad_request", null));
    }
  }

  // 댓글 삭제
  @DeleteMapping("/posts/{postId}/comments/{commentId}")
  public ResponseEntity<?> deleteComment(@PathVariable Long postId,
      @PathVariable Long commentId,
      HttpServletRequest request) {
    try {
      Long userId = (Long) request.getAttribute("userId");

      log.info(">> Controller: userId={}", userId);
      log.info(">> Controller: commentId={}", commentId);
      log.info(">> Controller: postId={}", postId);

      commentService.deleteComment(postId, commentId, userId);

      return ResponseEntity.ok(
          new ApiResponse("comment_delete_success", Map.of("redirectURL", "/posts/" + postId)));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body(new ApiResponse("comment_not_found", null));
    } catch (AccessDeniedException e) {
      return ResponseEntity.status(403).body(new ApiResponse("comment_forbidden", null));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new ApiResponse("comment_bad_request", null));
    }
  }

  // 댓글 목록 조회
  @GetMapping("/posts/{postId}/comments")
  public ResponseEntity<?> getComments(@PathVariable Long postId) {
    try {
      List<Map<String, Object>> comments = commentService.getCommentsByPost(postId);

      return ResponseEntity.ok(new ApiResponse("comment_get_success", comments));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body(new ApiResponse("comment_not_found", null));
    } catch (Exception e) {
      return ResponseEntity.status(400).body(new ApiResponse("comment_not_found", null));
    }

  }
}
