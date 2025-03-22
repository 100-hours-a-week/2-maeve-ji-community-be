package com.example.ktb.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"userId", "token"})
public class LoginResponseDto {
    private Long userId;
    private String token;
}
