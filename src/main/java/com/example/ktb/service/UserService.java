package com.example.ktb.service;

import com.example.ktb.dto.UserDto;
import com.example.ktb.dto.response.GetUserProfileResponseDto;
import com.example.ktb.entity.User;
import com.example.ktb.repository.UserRepository;
import com.example.ktb.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j  // 로그찍자
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  // 회원가입
  @Transactional
  public UserDto register(UserDto userDto) {
    if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }

    User user = User.builder()
        .email(userDto.getEmail())
        .password(userDto.getPassword()) // TO-DO: 암호화
        .nickname(userDto.getNickname())
        .imgUrl(userDto.getImgUrl())
        .deleted(false)
        .build();

    return UserDto.fromEntity(userRepository.save(user));
  }

  // 로그인 -> JWT 발급
  public String login(String email, String password) {
    log.info("로그인 시도 - email: {}, password: {}", email, password);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.error("로그인 실패 - 이메일 없음");
          return new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        });

    log.info("DB에서 가져온 유저 정보 - email: {}, password: {}", user.getEmail(), user.getPassword());

    if (!user.getPassword().equals(password)) {
      log.error("로그인 실패 - 비밀번호 불일치");
      throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    log.info("로그인 성공 - JWT 발급 시작");
    return jwtTokenProvider.createToken(user.getUserId(), user.getEmail());
  }


  // 회원Id -> 회원 객체
  public GetUserProfileResponseDto getUserProfile(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    return GetUserProfileResponseDto.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .imgUrl(user.getImgUrl())
        .build();

  }


  // 이메일 -> 회원Id만
  public Long getUserIdByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    return user.getUserId();
  }

  // 회원 정보 수정
  @Transactional
  public void updateUser(Long userId, UserDto userDto) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    user.setNickname(userDto.getNickname());
    log.info("userDto.getImgUrl(): {}", userDto.getImgUrl());
    log.info("user.getImgUrl(): {}", user.getImgUrl());
    if (userDto.getImgUrl() != null) {
      user.setImgUrl(userDto.getImgUrl());
    }
  }

  // 비밀번호 수정
  @Transactional
  public void updatePassword(Long userId, String password) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    user.setPassword(password);
  }

  // 회원 탈퇴
  @Transactional
  public boolean deleteUser(Long userId) {
    System.out.println(">>>> deleteUser userId: " + userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("user_not_found"));

    if (Boolean.TRUE.equals(user.getDeleted())) {
      System.out.println(">>>> 이미 삭제된 유저");
      return false;
    }

    user.setDeleted(true);
    System.out.println(">>>> 삭제 처리 완료");
    return true;
  }
}
