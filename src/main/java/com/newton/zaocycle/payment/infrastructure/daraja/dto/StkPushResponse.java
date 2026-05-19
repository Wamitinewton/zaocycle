package com.newton.zaocycle.payment.infrastructure.daraja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StkPushResponse(
        @JsonProperty("MerchantRequestID")  String merchantRequestId,
        @JsonProperty("CheckoutRequestID")  String checkoutRequestId,
        @JsonProperty("ResponseCode")       String responseCode,
        @JsonProperty("ResponseDescription") String responseDescription,
        @JsonProperty("CustomerMessage")    String customerMessage
) {}
