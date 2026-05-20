package com.newton.zaocycle.payment.infrastructure.daraja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DarajaTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") int expiresIn
) {
}
