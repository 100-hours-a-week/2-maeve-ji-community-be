package com.example.ktb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Lob    // longtext
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;


    @Column(name = "img_url", length = 255)
    private String imgUrl;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder
    public Post(User user, String title, String content, String imgUrl) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.createdAt = LocalDateTime.now();  // 글 생성 시 자동 입력
    }
}
