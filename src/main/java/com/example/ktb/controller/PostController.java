package com.example.ktb.controller;

import com.example.ktb.dto.PostDto;
import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.repository.PostRepository;
import com.example.ktb.service.PostService;
import com.example.ktb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final UserService userService;
  private final PostRepository postRepository;

  // 게시물 전체 조회
  @GetMapping("/posts")
  @CrossOrigin(origins = "*")
  public ResponseEntity<?> getAllPosts(HttpServletRequest request) {
    try {
      List<Map<String, Object>> posts = postService.getAllPosts();
      return ResponseEntity.ok(new ApiResponse("posts_all_get_success", Map.of("posts", posts)));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("message", "post_bad_request", "data", null));
    }
  }


  // 게시물 등록
  @PostMapping("/posts")
  public ResponseEntity<?> createPost(
      @RequestPart("title") String title,
      @RequestPart("content") String content,
      @RequestPart(value = "imgUrl", required = false) MultipartFile image,
      HttpServletRequest request) {

    try {
      Long userId = (Long) request.getAttribute("userId"); // 아예 Long으로 받음
      if (userId == null) {
        log.info("userId is null");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("message", "unauthorized", "data", null));
      }

      log.info("title: {}", title);
      log.info("content: {}", content);
      log.info("userId: {}", userId);
      log.info("image: {}", image);

      String imageUrl = null;
      if (image != null && !image.isEmpty()) {
        String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
        String uploadDir = System.getProperty("user.dir") + "/uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
          dir.mkdirs();
        }
        image.transferTo(new File(uploadDir + fileName));
        imageUrl = "http://localhost:8080/images/" + fileName;
      }

      PostDto postDto = new PostDto();
      postDto.setTitle(title);
      postDto.setContent(content);
      postDto.setImgUrl(imageUrl);

      Long postId = postService.createPost(postDto, userId);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("post_create_success", Map.of("postId", postId)));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Map.of("message", "post_bad_request", "data", null));
    }
  }


  // 게시물 조회
  @GetMapping("/posts/{postId}")
  public ResponseEntity<?> getPost(@PathVariable Long postId) {
    try {
      Map<String, Object> postData = postService.getPostDetail(postId);

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("post_get_success", Map.of("data", postData)));
//            return ResponseEntity.ok(Map.of(
//                    "message", "post_get_success",
//                    "data", postData
//            ));
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

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse("post_delete_success", Map.of("redirectURL", "/posts")));
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
//
//    // 게시물 좋아요
//    @PostMapping("/posts/{postId}/likes")
//    public ResponseEntity<?> likePost(@PathVariable Long postId, HttpServletRequest request) {
//        try {
//            Long userId = Long.parseLong((String) request.getAttribute("userId"));  // JWT에서 추출
//            postService.likePost(postId, userId);
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(Map.of("message", "like_success", "data", Map.of("postId", postId)));
//        } catch (IllegalArgumentException e) {
//            String message = e.getMessage().equals("삭제된 게시글입니다.") ? "deleted_post" : "post_not_found";
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "post_not_found", "data", null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("message", "like_bad_request", "data", null));
//        }
//    }
//
//
//    // 게시물 좋아요 삭제
//    @DeleteMapping("/posts/{postId}/likes")
//    public ResponseEntity<?> unlikePost(@PathVariable Long postId, HttpServletRequest request) {
//        try {
//            // 보통 JWT에서 userId 추출해서 좋아요 누른 사람인지 검증도 가능
//            Long userId = Long.parseLong((String) request.getAttribute("userId")); // JWT에서 추출
//            postService.unlikePost(postId, userId);
//            return ResponseEntity.noContent().build(); // 204 성공
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of("message", "like_not_found", "data", null));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("message", "like_bad_request", "data", null));
//        }
//    }
//

}
