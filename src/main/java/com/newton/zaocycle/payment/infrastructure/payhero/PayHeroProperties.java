package com.newton.zaocycle.payment.infrastructure.payhero;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payhero")
public record PayHeroProperties(
        String baseUrl,
        String basicAuthToken,
        int channelId,
        String stkCallbackUrl,
        String withdrawCallbackUrl
) {
}
