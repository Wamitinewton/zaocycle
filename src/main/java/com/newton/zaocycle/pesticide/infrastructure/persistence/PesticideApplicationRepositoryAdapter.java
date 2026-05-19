package com.newton.zaocycle.pesticide.infrastructure.persistence;

import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.pesticide.domain.port.PesticideApplicationRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class PesticideApplicationRepositoryAdapter implements PesticideApplicationRepository {

    private final PesticideApplicationJpaRepository jpa;

    PesticideApplicationRepositoryAdapter(PesticideApplicationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public PesticideApplication save(PesticideApplication application) {
        return PesticideApplicationEntityMapper.toDomain(
                jpa.save(PesticideApplicationEntityMapper.toEntity(application)));
    }

    @Override
    public Optional<PesticideApplication> findById(UUID id) {
        return jpa.findById(id).map(PesticideApplicationEntityMapper::toDomain);
    }

    @Override
    public List<PesticideApplication> findByFarmerId(UUID farmerId) {
        return jpa.findByFarmerId(farmerId).stream()
                .map(PesticideApplicationEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<PesticideApplication> findPendingByFarmerId(UUID farmerId) {
        return jpa.findByFarmerIdAndStatus(farmerId, "PENDING").stream()
                .map(PesticideApplicationEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<PesticideApplication> findByFarmerIdAndAppliedAtAfter(UUID farmerId, Instant since) {
        return jpa.findByFarmerIdAndAppliedAtAfter(farmerId, since).stream()
                .map(PesticideApplicationEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<PesticideApplication> findByFarmerIdAndStatus(UUID farmerId, ApplicationStatus status) {
        return jpa.findByFarmerIdAndStatus(farmerId, status.name()).stream()
                .map(PesticideApplicationEntityMapper::toDomain)
                .toList();
    }
}
