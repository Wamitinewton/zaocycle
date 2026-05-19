package com.newton.zaocycle.auth.api.dto;

import com.newton.zaocycle.auth.application.command.CreateStaffCommand;
import com.newton.zaocycle.auth.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStaffRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String fullName,
        @NotNull Role role
) {
    public CreateStaffCommand toCommand() {
        return new CreateStaffCommand(email, password, fullName, role);
    }
}
