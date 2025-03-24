package com.example.ktb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean isDeleted;


    @Data
    @Builder
    public static class CommentResponse {
        private Long id;
        private String comment;
        private AuthorDto author;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private Boolean isDeleted;

        @Data
        @Builder
        public static class AuthorDto {
            private Long userId;
            private String nickname;
            private String imgUrl;
        }
    }
//    // Entity -> DTO
//    public static CommentDto fromEntity(CommentDto commentDto) {
//        return CommentDto.builder()
//                .commentId(commentDto.getCommentId())
//                .postId(commentDto.getPostId())
//                .userId(commentDto.getUserId())
//                .comment(commentDto.getComment())
//                .createdAt(commentDto.getCreatedAt())
//                .modifiedAt(commentDto.getModifiedAt())
//                .isDeleted(commentDto.getIsDeleted())
//                .build();
//    }

}
