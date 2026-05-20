package com.newton.zaocycle.notification.infrastructure.africastalking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AtRecipientDto(
        @JsonProperty("number") String number,
        @JsonProperty("status") String status,
        @JsonProperty("statusCode") int statusCode,
        @JsonProperty("messageId") String messageId,
        @JsonProperty("cost") String cost
) {
}
