package com.newton.zaocycle.inventory.application.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateBatchCommand(
        String batchNumber,
        BigDecimal kgProduced,
        Instant producedAt,
        UUID sourceIntakeId
) {}
