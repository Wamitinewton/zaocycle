package com.newton.zaocycle.collection.api.dto;

import com.newton.zaocycle.collection.domain.model.FarmerEarnings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FarmerEarningsResponse(
        BigDecimal total,
        BigDecimal thisMonth,
        long pickupCount,
        List<PayoutEntryDto> recentPayouts
) {
    public static FarmerEarningsResponse from(FarmerEarnings earnings) {
        List<PayoutEntryDto> recent = earnings.recentPayouts().stream()
                .map(e -> new PayoutEntryDto(e.amount(), e.date()))
                .toList();
        return new FarmerEarningsResponse(
                earnings.total(), earnings.thisMonth(), earnings.pickupCount(), recent);
    }

    public record PayoutEntryDto(BigDecimal amount, LocalDate date) {
    }
}
