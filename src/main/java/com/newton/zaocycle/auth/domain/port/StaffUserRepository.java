package com.newton.zaocycle.auth.domain.port;

import com.newton.zaocycle.auth.domain.model.StaffUser;

import java.util.Optional;

public interface StaffUserRepository {
    Optional<StaffUser> findByEmail(String email);
    StaffUser save(StaffUser user);
}
