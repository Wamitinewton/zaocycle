package com.newton.zaocycle.certification.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
class CertificateEntity {

    @Id
    private UUID id;

    @Column(name = "application_id", nullable = false, unique = true)
    private UUID applicationId;

    @Column(nullable = false, unique = true, length = 32)
    private String token;

    @Column(name = "qr_image_url", nullable = false, columnDefinition = "TEXT")
    private String qrImageUrl;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "verified_count", nullable = false)
    private int verifiedCount;

    @Column(name = "last_verified_at")
    private Instant lastVerifiedAt;

    @Column(nullable = false, length = 20)
    private String status;
}
