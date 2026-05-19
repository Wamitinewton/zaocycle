package com.newton.zaocycle.inventory.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RecordIntakeCommand(
        LocalDate intakeDate,
        BigDecimal totalKg,
        List<UUID> pickupIds,
        String notes,
        UUID recordedBy
) {}
