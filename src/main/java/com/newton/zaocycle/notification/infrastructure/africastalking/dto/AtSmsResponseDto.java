package com.newton.zaocycle.notification.infrastructure.africastalking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AtSmsResponseDto(
        @JsonProperty("SMSMessageData") AtSmsMessageDataDto smsMessageData
) {}
