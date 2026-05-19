package com.newton.zaocycle.auth.api.dto;

import com.newton.zaocycle.auth.domain.model.StaffUser;

import java.time.Instant;

public record StaffResponse(
        String id,
        String email,
        String fullName,
        String role,
        boolean active,
        String profileImageUrl,
        Instant createdAt
) {
    public static StaffResponse from(StaffUser user) {
        return new StaffResponse(
                user.id().toString(),
                user.email(),
                user.fullName(),
                user.role().name(),
                user.isActive(),
                user.profileImageUrl(),
                user.createdAt()
        );
    }
}
