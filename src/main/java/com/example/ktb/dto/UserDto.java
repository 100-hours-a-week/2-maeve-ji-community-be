package com.example.ktb.dto;

import com.example.ktb.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String email;
    private String password;
    private String nickname;
    @JsonProperty("img_url")
    private String imgUrl;
    private Boolean deleted;

    // Entity -> DTO
    public static UserDto fromEntity(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .nickname(user.getNickname())
                .imgUrl(user.getImgUrl())

                .deleted(user.getDeleted())
                .build();
    }
}
