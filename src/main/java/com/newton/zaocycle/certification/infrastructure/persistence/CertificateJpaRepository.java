package com.newton.zaocycle.certification.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface CertificateJpaRepository extends JpaRepository<CertificateEntity, UUID> {
    Optional<CertificateEntity> findByToken(String token);

    Optional<CertificateEntity> findByApplicationId(UUID applicationId);
}
