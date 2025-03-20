package com.example.ktb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 외부에서 막 생성하지 못하게
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Builder
    public Comment(Post post, User user, String comment) {
        this.post = post;
        this.user = user;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
    }

    // 댓글 수정 메서드
    public void update(String comment) {
        this.comment = comment;
        this.modifiedAt = LocalDateTime.now();
    }

    // 소프트 삭제
    public void delete() {
        this.isDeleted = true;
    }
}