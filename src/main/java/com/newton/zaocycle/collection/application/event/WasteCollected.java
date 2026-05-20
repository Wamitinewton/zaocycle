package com.newton.zaocycle.collection.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record WasteCollected(
        UUID pickupId,
        UUID farmerId,
        UUID riderId,
        BigDecimal weightKg,
        BigDecimal payoutAmount
) {
}
