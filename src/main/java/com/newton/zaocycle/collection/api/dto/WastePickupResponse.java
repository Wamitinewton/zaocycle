package com.newton.zaocycle.collection.api.dto;

import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record WastePickupResponse(
        UUID id,
        UUID farmerId,
        UUID riderId,
        UUID applicationId,
        Instant requestedAt,
        LocalDate scheduledFor,
        PickupStatus status,
        BigDecimal weightKg,
        String photoUrl,
        String notes,
        BigDecimal payoutAmount,
        Instant collectedAt,
        Instant paidAt,
        Instant createdAt
) {
    public static WastePickupResponse from(WastePickup pickup) {
        return new WastePickupResponse(
                pickup.id(), pickup.farmerId(), pickup.riderId(), pickup.applicationId(),
                pickup.requestedAt(), pickup.scheduledFor(), pickup.status(),
                pickup.weightKg(), pickup.photoUrl(), pickup.notes(),
                pickup.payoutAmount(), pickup.collectedAt(), pickup.paidAt(),
                pickup.createdAt()
        );
    }
}
