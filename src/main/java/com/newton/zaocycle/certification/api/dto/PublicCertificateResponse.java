package com.newton.zaocycle.certification.api.dto;

import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;

public record PublicCertificateResponse(
        String token,
        String status,
        String issuedAt,
        String expiresAt,
        int verifiedCount,
        String crop,
        String chemicalName,
        String farmerName,
        String ward
) {
    public static PublicCertificateResponse from(Certificate cert, PesticideApplication application,
                                                  String chemicalName, String farmerName, String ward) {
        return new PublicCertificateResponse(
                cert.token(),
                cert.status().name(),
                cert.issuedAt().toString(),
                cert.expiresAt().toString(),
                cert.verifiedCount(),
                application.crop(),
                chemicalName,
                farmerName,
                ward
        );
    }
}
