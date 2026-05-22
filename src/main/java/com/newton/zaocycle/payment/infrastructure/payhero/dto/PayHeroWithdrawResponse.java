package com.newton.zaocycle.payment.infrastructure.payhero.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayHeroWithdrawResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("external_reference") String externalReference,
        @JsonProperty("transaction_id") String transactionId
) {
}
