package com.newton.zaocycle.payment.infrastructure.payhero.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PayHeroWithdrawCallback(
        @JsonProperty("status") String status,
        @JsonProperty("external_reference") String externalReference,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("transaction_id") String transactionId
) {
}
