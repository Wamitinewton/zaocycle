package com.newton.zaocycle.auth.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        int accessTokenMinutes,
        int refreshTokenDays
) {}
