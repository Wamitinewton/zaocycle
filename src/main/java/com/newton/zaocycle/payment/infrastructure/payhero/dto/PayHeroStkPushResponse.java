package com.newton.zaocycle.payment.infrastructure.payhero.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayHeroStkPushResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("CheckoutRequestID") String checkoutRequestId,
        @JsonProperty("MerchantRequestID") String merchantRequestId
) {
}
