package com.newton.zaocycle.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record FarmerLoginRequest(
        @NotBlank String phone,
        @NotBlank String pin
) {}
