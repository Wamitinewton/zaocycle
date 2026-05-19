package com.newton.zaocycle.certification.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class Certificate {

    private final UUID id;
    private final UUID applicationId;
    private final String token;
    private final String qrImageUrl;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private int verifiedCount;
    private Instant lastVerifiedAt;
    private CertificateStatus status;

    public Certificate(UUID id, UUID applicationId, String token, String qrImageUrl,
                       Instant issuedAt, Instant expiresAt, int verifiedCount,
                       Instant lastVerifiedAt, CertificateStatus status) {
        this.id = id;
        this.applicationId = applicationId;
        this.token = token;
        this.qrImageUrl = qrImageUrl;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.verifiedCount = verifiedCount;
        this.lastVerifiedAt = lastVerifiedAt;
        this.status = status;
    }

    public static Certificate create(UUID applicationId, String token, String qrImageUrl,
                                     Instant issuedAt, Instant expiresAt) {
        return new Certificate(IdGenerator.generate(), applicationId, token, qrImageUrl,
                issuedAt, expiresAt, 0, null, CertificateStatus.ACTIVE);
    }

    public void recordVerification() {
        this.verifiedCount++;
        this.lastVerifiedAt = Instant.now();
    }

    public void revoke() {
        this.status = CertificateStatus.REVOKED;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public UUID id()                    { return id; }
    public UUID applicationId()         { return applicationId; }
    public String token()               { return token; }
    public String qrImageUrl()          { return qrImageUrl; }
    public Instant issuedAt()           { return issuedAt; }
    public Instant expiresAt()          { return expiresAt; }
    public int verifiedCount()          { return verifiedCount; }
    public Instant lastVerifiedAt()     { return lastVerifiedAt; }
    public CertificateStatus status()   { return status; }
}
