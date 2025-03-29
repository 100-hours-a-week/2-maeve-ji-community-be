package com.example.ktb.controller;

import com.example.ktb.dto.UserDto;
import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.dto.response.LoginResponseDto;
import com.example.ktb.service.AuthService;
import com.example.ktb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final AuthService authService;

  // 회원가입
  @PostMapping("/users")
  @CrossOrigin(origins = "*")
  public ResponseEntity<?> register(
      @RequestPart("userDto") UserDto userDto,
      @RequestPart(value = "profileImage") MultipartFile profileImage) {

    // 이미지 저장 처리
    if (profileImage != null && !profileImage.isEmpty()) {
      String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
      String uploadDir = System.getProperty("user.dir") + "/uploads/";
      String savePath = uploadDir + fileName;
      try {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
          dir.mkdirs();  // 폴더 생성
        }
        profileImage.transferTo(new File(savePath));
        String imageUrl = "http://localhost:8080/images/" + fileName;
        userDto.setImgUrl(imageUrl); // 이미지 URL userDto에 주입
      } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("이미지 저장 실패");
      }
    }

    // DB 저장 처리
    UserDto savedUser = userService.register(userDto);
    return ResponseEntity.status(201).body(new ApiResponse("user_signin_success", savedUser));
  }

  // 로그인
  @PostMapping("/auth/sessions")
  @CrossOrigin(origins = "*")
  public ResponseEntity<?> login(@RequestBody UserDto userDto) {
    String token = userService.login(userDto.getEmail(), userDto.getPassword());
    Long userId = userService.getUserIdByEmail(userDto.getEmail());
    Map<String, Object> data = new HashMap<>();
    LoginResponseDto responseDto = new LoginResponseDto(userId, token);
    return ResponseEntity.status(201).body(new ApiResponse("user_login_success", responseDto));
  }

  // 로그아웃
  @DeleteMapping("/auth/sessions")
  public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    authService.logout(token);

    Map<String, String> data = Map.of("redirectURL", "/auth/login");
    return ResponseEntity.ok(new ApiResponse("user_logout_success", data));
  }

  // 회원 조회
  @GetMapping("/users/{userId}")
  @CrossOrigin(origins = "http://127.0.0.1:5500", allowCredentials = "true")
  public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
    return ResponseEntity.ok(
        new ApiResponse("user_profile_success", userService.getUserProfile(userId)));
  }

  // 게시글 수정
  @PatchMapping("/users/{userId}")
  public ResponseEntity<?> updateUser(
      @PathVariable Long userId,
      @RequestPart("userDto") UserDto userDto,
      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
      HttpServletRequest request) {

    Long authenticatedUserId = (Long) request.getAttribute("userId");
    if (!authenticatedUserId.equals(userId)) {
      return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).build();
    }

    if (userDto == null) {
      return ResponseEntity.badRequest().body("userDto가 전달되지 않았습니다.");
    }

    System.out.println("nickname: " + userDto.getNickname());
    System.out.println("img_url(before): " + userDto.getImgUrl());

    if (profileImage != null && !profileImage.isEmpty()) {
      String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
      String uploadDir = System.getProperty("user.dir") + "/uploads/";
      String savePath = uploadDir + fileName;

      try {
        File dir = new File(uploadDir);
        if (!dir.exists()) {
          dir.mkdirs();
        }

        profileImage.transferTo(new File(savePath));
        String imageUrl = "http://localhost:8080/images/" + fileName;
        userDto.setImgUrl(imageUrl);
        System.out.println("img_url(after): " + imageUrl);
      } catch (IOException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            .body("이미지 저장 실패");
      }
    }

    userService.updateUser(userId, userDto);
    return ResponseEntity.noContent().build();
  }


  // 비밀번호 수정
  @PatchMapping("/users/{userId}/password")
  public ResponseEntity<?> updatePassword(@PathVariable Long userId,
      @RequestBody UserDto userDto,
      HttpServletRequest request) {
    // JWT에서 인증된 userId 꺼내기
    Long authenticatedUserId = (Long) request.getAttribute("userId");

    System.out.println("authenticatedUserId: " + authenticatedUserId);
    System.out.println("requested userId: " + userId);

    // 본인 맞는지 확인
    if (!authenticatedUserId.equals(userId)) {
      return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN)
          .body(Map.of("message", "user_forbidden", "data", null));
    }

    try {
      userService.updatePassword(userId, userDto.getPassword());
      return ResponseEntity.noContent().build(); // 204 성공
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(404).body(new ApiResponse("user_not_found", null));

    } catch (Exception e) {
      return ResponseEntity.status(500).body(new ApiResponse("internal_server_error", null));
    }
  }

  // 회원 탈퇴
  @DeleteMapping("/users/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable Long userId,
      HttpServletRequest request) {
    Long authenticatedUserId = (Long) request.getAttribute("userId");
    System.out.println(">>>> deleteUser userId: " + userId);

    if (!authenticatedUserId.equals(userId)) {
      Map<String, Object> body = new HashMap<>();
      body.put("message", "user_forbidden");
      body.put("data", null);
      return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(body);
    }

    try {
      boolean deleted = userService.deleteUser(userId);
      if (!deleted) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "user_already_deleted");
        body.put("data", null);
        return ResponseEntity.status(400).body(body);
      }

      Map<String, Object> successBody = new LinkedHashMap<>();
      successBody.put("message", "user_delete_success");
      successBody.put("data", Map.of("redirectURL", "/auth/login"));

      return ResponseEntity.ok(successBody); // 200 성공
    } catch (IllegalArgumentException e) {
      Map<String, Object> body = new HashMap<>();
      body.put("message", "user_not_found");
      body.put("data", null);
      return ResponseEntity.status(404).body(body);
    } catch (Exception e) {
      Map<String, Object> body = new HashMap<>();
      body.put("message", "internal_server_error");
      body.put("data", null);
      return ResponseEntity.status(500).body(body);
    }
  }

}