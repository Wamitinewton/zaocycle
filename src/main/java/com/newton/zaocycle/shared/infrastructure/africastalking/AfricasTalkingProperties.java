package com.newton.zaocycle.shared.infrastructure.africastalking;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "africastalking")
public record AfricasTalkingProperties(
        String apiKey,
        String username,
        String senderId,
        String baseUrl,
        String serviceCode
) {}
