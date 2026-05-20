package com.newton.zaocycle.inventory.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record BuyerOrderPlaced(
        UUID orderId,
        UUID buyerId,
        UUID productId,
        BigDecimal totalAmount,
        BigDecimal totalKg
) {
}
