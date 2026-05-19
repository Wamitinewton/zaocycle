package com.newton.zaocycle.auth.api.dto;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        int expiresIn,
        UserSummary user
) {
    public record UserSummary(String id, String role, String displayName) {}
}
