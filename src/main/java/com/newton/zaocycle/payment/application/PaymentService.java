package com.newton.zaocycle.payment.application;

import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroStkCallback;
import com.newton.zaocycle.payment.infrastructure.payhero.dto.PayHeroWithdrawCallback;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    MpesaTransaction initiateB2C(UUID pickupId, String phone, BigDecimal amount);

    MpesaTransaction initiateStkPush(UUID orderId, String phone, BigDecimal amount, String reference);

    void handleStkCallback(PayHeroStkCallback callback);

    void handleWithdrawCallback(PayHeroWithdrawCallback callback);
}
