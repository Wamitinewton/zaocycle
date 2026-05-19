package com.newton.zaocycle.auth.infrastructure.persistence;

import com.newton.zaocycle.auth.domain.model.Role;
import com.newton.zaocycle.auth.domain.model.StaffUser;

final class StaffUserEntityMapper {

    private StaffUserEntityMapper() {}

    static StaffUser toDomain(StaffUserEntity e) {
        StaffUser user = new StaffUser(
                e.getId(),
                e.getEmail(),
                e.getPasswordHash(),
                e.getFullName(),
                Role.valueOf(e.getRole()),
                e.isActive(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
        if (e.getProfileImageUrl() != null) {
            user.updateProfileImage(e.getProfileImageUrl());
        }
        return user;
    }

    static StaffUserEntity toEntity(StaffUser user) {
        StaffUserEntity e = new StaffUserEntity();
        e.setId(user.id());
        e.setEmail(user.email());
        e.setPasswordHash(user.passwordHash());
        e.setFullName(user.fullName());
        e.setRole(user.role().name());
        e.setActive(user.isActive());
        e.setProfileImageUrl(user.profileImageUrl());
        return e;
    }
}
