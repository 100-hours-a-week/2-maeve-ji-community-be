package com.example.ktb.dto;

import com.example.ktb.entity.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long likeId;
    private Long userId;
    private Long postId;

    public static LikeDto fromEntity(Like like) {
        return LikeDto.builder()
                .likeId(like.getLikeId())
                .userId(like.getUser().getUserId())
                .postId(like.getPost().getPostId())
                .build();
    }
}
