package com.example.ktb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // JPA에서 관리하는 Entity
@Table(name = "Users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 외부에서 막 생성하지 못하게
public class User {
    @Id // PK
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동증가
    private Long userId;

    // 회원 이메일
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // 회원 비밀번호
    @Column(name = "password", length = 25, nullable = false)
    private String password;

    // 회원 닉네임
    @Column(name = "nickname", length = 10, unique = true, nullable = false)
    private String nickname;

    // 회원 프로필사진
    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    // 회원삭제
    @Column(name = "is_deleted")
    private boolean isDeleted = false;  // 기본 false로 초기화

    @Builder
    public User(String email, String password, String nickname, String imgUrl) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.imgUrl = imgUrl;
    }

    public void delete() {
        this.isDeleted = true;
    }
}
