package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.application.command.CreateStaffCommand;
import com.newton.zaocycle.auth.domain.model.StaffUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StaffService {
    StaffUser create(CreateStaffCommand cmd);
    List<StaffUser> findAll();
    Optional<StaffUser> findById(UUID id);
    StaffUser deactivate(UUID id);
    StaffUser reactivate(UUID id);
    StaffUser updateProfileImage(UUID id, String imageUrl);
}
