package com.newton.zaocycle.payment.infrastructure.daraja;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "daraja")
public record DarajaProperties(
        String baseUrl,
        String consumerKey,
        String consumerSecret,
        String shortcode,
        String initiatorName,
        String securityCredential,
        String b2cResultUrl,
        String b2cTimeoutUrl,
        String stkPasskey,
        String stkCallbackUrl
) {}
