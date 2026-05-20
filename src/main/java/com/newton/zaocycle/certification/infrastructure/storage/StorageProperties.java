package com.newton.zaocycle.certification.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "zaocycle.storage")
public record StorageProperties(
        String provider,
        LocalFs localFs,
        Spaces spaces
) {
    public record LocalFs(String basePath, String publicUrlPrefix) {
    }

    public record Spaces(String endpoint, String region, String bucket, String accessKey, String secretKey) {
    }
}
