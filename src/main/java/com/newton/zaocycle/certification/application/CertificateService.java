package com.newton.zaocycle.certification.application;

import com.newton.zaocycle.certification.domain.model.Certificate;

import java.util.Optional;
import java.util.UUID;

public interface CertificateService {
    Certificate issueFor(UUID applicationId);

    Optional<Certificate> findById(UUID id);

    Optional<Certificate> findByToken(String token);

    void recordVerification(UUID certId);

    Certificate revoke(UUID certId);
}
