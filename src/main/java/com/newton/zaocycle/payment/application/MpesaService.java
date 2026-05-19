package com.newton.zaocycle.payment.application;

import com.newton.zaocycle.payment.domain.model.MpesaTransaction;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.B2CResultCallback;
import com.newton.zaocycle.payment.infrastructure.daraja.dto.StkPushCallback;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface MpesaService {
    MpesaTransaction initiateB2C(UUID pickupId, String phone, BigDecimal amount);
    MpesaTransaction initiateStkPush(UUID orderId, String phone, BigDecimal amount, String reference);
    void handleB2CResult(B2CResultCallback callback);
    void handleB2CTimeout(Map<String, Object> body);
    void handleStkPushCallback(StkPushCallback callback);
}
