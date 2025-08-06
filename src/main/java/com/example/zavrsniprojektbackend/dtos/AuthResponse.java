package com.example.zavrsniprojektbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("token_type")
        String tokenType,

        UserInfoDto user
) {
    public static AuthResponse of(String accessToken, String refreshToken, UserInfoDto user) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", user);
    }
}