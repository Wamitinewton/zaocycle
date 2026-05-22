package com.newton.zaocycle.payment.infrastructure.payhero.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PayHeroStkCallback(
        @JsonProperty("status") String status,
        @JsonProperty("external_reference") String externalReference,
        @JsonProperty("response") StkCallbackResponse response
) {
    public record StkCallbackResponse(
            @JsonProperty("ResultCode") String resultCode,
            @JsonProperty("ResultDesc") String resultDesc,
            @JsonProperty("CheckoutRequestID") String checkoutRequestId,
            @JsonProperty("MpesaReceiptNumber") String mpesaReceiptNumber,
            @JsonProperty("Amount") BigDecimal amount
    ) {
    }
}
