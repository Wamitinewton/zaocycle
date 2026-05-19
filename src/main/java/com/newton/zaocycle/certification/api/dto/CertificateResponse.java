package com.newton.zaocycle.certification.api.dto;

import com.newton.zaocycle.certification.domain.model.Certificate;

public record CertificateResponse(
        String id,
        String applicationId,
        String token,
        String qrImageUrl,
        String status,
        String issuedAt,
        String expiresAt,
        int verifiedCount
) {
    public static CertificateResponse from(Certificate cert) {
        return new CertificateResponse(
                cert.id().toString(),
                cert.applicationId().toString(),
                cert.token(),
                cert.qrImageUrl(),
                cert.status().name(),
                cert.issuedAt().toString(),
                cert.expiresAt().toString(),
                cert.verifiedCount()
        );
    }
}
