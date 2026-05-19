package com.newton.zaocycle.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record RiderLoginRequest(
        @NotBlank String phone,
        @NotBlank String password
) {}
