package com.newton.zaocycle.inventory.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RecordIntakeRequest(
        @NotNull LocalDate intakeDate,
        @NotNull @Positive BigDecimal totalKg,
        @NotNull List<UUID> pickupIds,
        String notes
) {
}
