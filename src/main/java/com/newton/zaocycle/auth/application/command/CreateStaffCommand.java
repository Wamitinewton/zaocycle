package com.newton.zaocycle.auth.application.command;

import com.newton.zaocycle.auth.domain.model.Role;

public record CreateStaffCommand(String email, String password, String fullName, Role role) {}
