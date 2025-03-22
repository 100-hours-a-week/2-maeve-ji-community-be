package com.example.ktb.dto;

import com.example.ktb.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long postId;
    private Long user;
    private String title;
    private String content;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean isDeleted;
    private int postView;
    private int postLike;
    private int postComment;

    // Entity -> DTO 변환
    public static PostDto fromEntity(Post post) {
        return PostDto.builder()
                .postId(post.getPostId())
                .user(post.getUser().getUserId())  // User 객체에서 userId 추출
                .title(post.getTitle())
                .content(post.getContent())
                .imgUrl(post.getImgUrl())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .isDeleted(post.getIsDeleted())
                .postView(post.getPostView())
                .postLike(post.getPostLike())
                .postComment(post.getPostComment())
                .build();
    }
}
