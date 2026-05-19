package com.newton.zaocycle.notification.infrastructure.africastalking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AtSmsMessageDataDto(
        @JsonProperty("Message") String message,
        @JsonProperty("Recipients") List<AtRecipientDto> recipients
) {}
