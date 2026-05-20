package com.newton.zaocycle.payment.application.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OrderPaid(
        UUID orderId,
        UUID mpesaTransactionId,
        UUID buyerId,
        String buyerPhone,
        BigDecimal totalAmount,
        BigDecimal totalKg,
        LocalDate requestedDeliveryDate
) {
}
