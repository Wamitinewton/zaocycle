package com.newton.zaocycle.auth.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UnifiedLoginRequest(
        @NotBlank String identifier,
        @NotBlank String credential
) {}
