package com.newton.zaocycle.collection.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record FarmerEarnings(
        BigDecimal total,
        BigDecimal thisMonth,
        long pickupCount,
        List<PayoutEntry> recentPayouts
) {
}
