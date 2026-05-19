package com.newton.zaocycle.payment.application.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PayoutCompleted(
        UUID pickupId,
        UUID mpesaTransactionId,
        BigDecimal amount,
        String farmerPhone,
        String mpesaReceiptNumber,
        BigDecimal weightKg
) {}
