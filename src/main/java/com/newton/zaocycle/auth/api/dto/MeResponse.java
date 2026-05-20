package com.newton.zaocycle.auth.api.dto;

public record MeResponse(
        String id,
        String role,
        String displayName,
        String phone,
        String email
) {}
