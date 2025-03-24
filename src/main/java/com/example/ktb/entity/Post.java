package com.example.ktb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Entity
@Table(name = "Posts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @Lob
    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "post_view")
    private int postView = 0;

    @Column(name = "post_like")
    private int postLike = 0;

    @Column(name = "post_comment")
    private int postComment = 0;

    public void increaseCommentCount() {
        this.postComment += 1;
    }

    // 댓글 개수 삭제
    public void decreaseCommentCount() {
        this.postComment += 1;
        if (this.postComment < 0) {
            this.postComment = 0;
        }
    }

}
