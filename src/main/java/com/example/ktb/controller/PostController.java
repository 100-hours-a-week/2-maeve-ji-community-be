package com.example.ktb.controller;

import com.example.ktb.dto.PostDto;
import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.service.PostService;
import com.example.ktb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    // 게시물 등록
    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto, HttpServletRequest request) {
        try {
            Long userId = Long.parseLong((String) request.getAttribute("userId")); // JWT에서 추출
            Long postId = postService.createPost(postDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "post_create_success", "data", Map.of("post_id", postId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "post_bad_request", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "post_unauthorized", "data", null));
        }
    }

    // 게시물 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        try {
            Map<String, Object> postData = postService.getPostDetail(postId);
            return ResponseEntity.ok(Map.of(
                    "message", "post_get_success",
                    "data", postData
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "post_not_found", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "post_bad_request", "data", null));
        }
    }

    // 게시글 수정
    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody Map<String, String> updateRequest,
                                        HttpServletRequest request) {
        try {
            Long userId = Long.parseLong((String) request.getAttribute("userId")); // JWT에서 추출
            String title = updateRequest.get("title");
            String content = updateRequest.get("content");
            String imgUrl = updateRequest.get("img_url");

            postService.updatePost(postId, userId, title, content, imgUrl);
            return ResponseEntity.noContent().build(); // 204 성공
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "post_not_found", "data", null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "post_forbidden", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "post_bad_request", "data", null));
        }
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId,
                                        HttpServletRequest request) {
        try {
            Long userId = Long.parseLong((String) request.getAttribute("userId"));
            postService.deletePost(postId, userId);

            return ResponseEntity.ok(Map.of(
                    "message", "post_delete_success",
                    "data", Map.of("redirectURL", "/posts")
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "post_not_found", "data", null));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "post_forbidden", "data", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "post_bad_request", "data", null));
        }
    }

}
