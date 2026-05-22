package com.newton.zaocycle.payment.infrastructure.payhero.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PayHeroWithdrawRequest(
        @JsonProperty("network_code") String networkCode,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("account_id") int accountId,
        @JsonProperty("narration") String narration,
        @JsonProperty("provider") String provider,
        @JsonProperty("external_reference") String externalReference,
        @JsonProperty("callback_url") String callbackUrl
) {
}
