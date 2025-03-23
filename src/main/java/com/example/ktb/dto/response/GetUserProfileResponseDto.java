package com.example.ktb.dto.response;

import com.example.ktb.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
public class GetUserProfileResponseDto {
    private Long userId;
    private String email;
    private String nickname;
    private String imgUrl;

    // Entity -> DTO 변환용 메서드
    public static GetUserProfileResponseDto fromEntity(User user) {
        return GetUserProfileResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .imgUrl(user.getImgUrl())
                .build();
    }
}
