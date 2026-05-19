package com.newton.zaocycle.certification.domain.port;

import com.newton.zaocycle.certification.domain.model.Certificate;

import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository {
    Optional<Certificate> findById(UUID id);
    Optional<Certificate> findByToken(String token);
    Optional<Certificate> findByApplicationId(UUID applicationId);
    Certificate save(Certificate cert);
}
