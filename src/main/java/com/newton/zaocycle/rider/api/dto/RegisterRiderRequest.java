package com.newton.zaocycle.rider.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRiderRequest(
        @NotBlank String phone,
        @NotBlank String fullName,
        @NotBlank String ward,
        @NotBlank @Size(min = 6) String password
) {
}
