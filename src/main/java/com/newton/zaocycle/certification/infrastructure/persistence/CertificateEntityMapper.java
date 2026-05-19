package com.newton.zaocycle.certification.infrastructure.persistence;

import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.certification.domain.model.CertificateStatus;

class CertificateEntityMapper {

    private CertificateEntityMapper() {}

    static Certificate toDomain(CertificateEntity e) {
        return new Certificate(
                e.getId(),
                e.getApplicationId(),
                e.getToken(),
                e.getQrImageUrl(),
                e.getIssuedAt(),
                e.getExpiresAt(),
                e.getVerifiedCount(),
                e.getLastVerifiedAt(),
                CertificateStatus.valueOf(e.getStatus())
        );
    }

    static CertificateEntity toEntity(Certificate cert) {
        CertificateEntity e = new CertificateEntity();
        e.setId(cert.id());
        e.setApplicationId(cert.applicationId());
        e.setToken(cert.token());
        e.setQrImageUrl(cert.qrImageUrl());
        e.setIssuedAt(cert.issuedAt());
        e.setExpiresAt(cert.expiresAt());
        e.setVerifiedCount(cert.verifiedCount());
        e.setLastVerifiedAt(cert.lastVerifiedAt());
        e.setStatus(cert.status().name());
        return e;
    }
}
