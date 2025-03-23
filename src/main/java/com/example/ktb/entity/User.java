package com.example.ktb.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity // JPA에서 관리하는 Entity
@Table(name = "Users")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 외부에서 막 생성하지 못하게
@AllArgsConstructor
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
    @Lob
    @Column(name = "img_url", nullable = false)
    private String imgUrl;

    // 삭제 여부
    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

}
