package com.newton.zaocycle.notification.domain.port;

public record SmsDispatchResult(boolean success, String providerMessageId, String errorMessage) {}
