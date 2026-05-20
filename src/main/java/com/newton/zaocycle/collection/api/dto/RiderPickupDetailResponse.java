package com.newton.zaocycle.collection.api.dto;

import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.farmer.domain.model.Farmer;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record RiderPickupDetailResponse(
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
        Instant createdAt,
        Double pickupLatitude,
        Double pickupLongitude,
        String pickupTradingCenter,
        String farmerPhone
) {
    public static RiderPickupDetailResponse from(WastePickup pickup, Farmer farmer) {
        return new RiderPickupDetailResponse(
                pickup.id(),
                pickup.farmerId(),
                pickup.riderId(),
                pickup.applicationId(),
                pickup.requestedAt(),
                pickup.scheduledFor(),
                pickup.status(),
                pickup.weightKg(),
                pickup.photoUrl(),
                pickup.notes(),
                pickup.payoutAmount(),
                pickup.collectedAt(),
                pickup.paidAt(),
                pickup.createdAt(),
                farmer != null ? farmer.latitude() : null,
                farmer != null ? farmer.longitude() : null,
                farmer != null ? farmer.tradingCenter() : null,
                farmer != null ? farmer.phone().value() : null
        );
    }
}
