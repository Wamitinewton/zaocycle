package com.newton.zaocycle.inventory.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateBatchRequest(
        @NotBlank String batchNumber,
        @NotNull @Positive BigDecimal kgProduced,
        @NotNull Instant producedAt,
        UUID sourceIntakeId
) {
}
