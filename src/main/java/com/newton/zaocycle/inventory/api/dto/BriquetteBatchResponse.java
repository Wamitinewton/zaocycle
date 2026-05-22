package com.newton.zaocycle.inventory.api.dto;

import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BriquetteBatchResponse(
        UUID id,
        String batchNumber,
        BigDecimal kgProduced,
        BigDecimal kgRemaining,
        Instant producedAt,
        UUID sourceIntakeId,
        Instant createdAt
) {
    public static BriquetteBatchResponse from(BriquetteBatch b) {
        return new BriquetteBatchResponse(
                b.id(), b.batchNumber(), b.kgProduced(),
                b.kgRemaining(), b.producedAt(), b.sourceIntakeId(), b.createdAt());
    }
}
