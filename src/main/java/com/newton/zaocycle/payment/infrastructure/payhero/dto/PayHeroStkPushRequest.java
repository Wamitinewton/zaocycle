package com.newton.zaocycle.payment.infrastructure.payhero.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PayHeroStkPushRequest(
        @JsonProperty("amount") BigDecimal amount,
        @JsonProperty("phone_number") String phoneNumber,
        @JsonProperty("channel_id") int channelId,
        @JsonProperty("provider") String provider,
        @JsonProperty("external_reference") String externalReference,
        @JsonProperty("callback_url") String callbackUrl
) {
}
