package com.example.ktb.controller;

import com.example.ktb.dto.response.ApiResponse;
import com.example.ktb.dto.UserDto;
import com.example.ktb.dto.response.GetUserProfileResponseDto;
import com.example.ktb.dto.response.LoginResponseDto;
import com.example.ktb.service.AuthService;
import com.example.ktb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    // 회원가입
    @PostMapping("/users")
    public ResponseEntity<?> register(@RequestBody UserDto userDto) {
        System.out.println("Img url " + userDto.getImgUrl()) ;
        UserDto savedUser = userService.register(userDto);
        return ResponseEntity.status(201).body(new ApiResponse("user_signin_success", savedUser));
    }

    // 로그인
    @PostMapping("/auth/sessions")
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
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
//        GetUserProfileResponseDto responseDto = new GetUserProfileResponseDto(userId, email, nickname, em)
        return ResponseEntity.ok(new ApiResponse("user_profile_success", userService.getUserProfile(userId)));
    }

    // 회원 정보 수정
    @PatchMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userService.updateUser(userId, userDto);
        return ResponseEntity.noContent().build();
    }

    // 비밀번호 수정
    @PatchMapping("/users/{userId}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userService.updatePassword(userId, userDto.getPassword());
        return ResponseEntity.noContent().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse("user_delete_success", "/auth/login"));
    }
}
