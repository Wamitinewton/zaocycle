package com.newton.zaocycle.certification.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zaocycle.certification")
public record CertificationProperties(String verifyBaseUrl, int expiresAfterDays) {
}
