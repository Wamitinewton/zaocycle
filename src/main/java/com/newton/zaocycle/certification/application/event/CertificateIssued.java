package com.newton.zaocycle.certification.application.event;

import java.util.UUID;

public record CertificateIssued(
        UUID certId,
        UUID applicationId,
        UUID farmerId,
        String crop,
        String token,
        String qrImageUrl
) {
}
