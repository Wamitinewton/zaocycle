package com.newton.zaocycle.auth.domain.port;

import com.newton.zaocycle.auth.domain.model.StaffUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffUserRepository {
    Optional<StaffUser> findByEmail(String email);
    Optional<StaffUser> findById(UUID id);
    List<StaffUser> findAll();
    boolean existsByEmail(String email);
    StaffUser save(StaffUser user);
}
