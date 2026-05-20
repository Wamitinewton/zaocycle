package com.newton.zaocycle.farmer.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterFarmerRequest(
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{7,14}$") String phone,
        @NotBlank @Size(max = 255) String fullName,
        @NotBlank String ward,
        @NotBlank @Size(min = 4, max = 4) String pin
) {
}
