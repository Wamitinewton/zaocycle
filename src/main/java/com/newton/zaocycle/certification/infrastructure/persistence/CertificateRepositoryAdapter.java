package com.newton.zaocycle.certification.infrastructure.persistence;

import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.certification.domain.port.CertificateRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class CertificateRepositoryAdapter implements CertificateRepository {

    private final CertificateJpaRepository jpa;

    CertificateRepositoryAdapter(CertificateJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Certificate> findById(UUID id) {
        return jpa.findById(id).map(CertificateEntityMapper::toDomain);
    }

    @Override
    public Optional<Certificate> findByToken(String token) {
        return jpa.findByToken(token).map(CertificateEntityMapper::toDomain);
    }

    @Override
    public Optional<Certificate> findByApplicationId(UUID applicationId) {
        return jpa.findByApplicationId(applicationId).map(CertificateEntityMapper::toDomain);
    }

    @Override
    public Certificate save(Certificate cert) {
        return CertificateEntityMapper.toDomain(jpa.save(CertificateEntityMapper.toEntity(cert)));
    }
}
