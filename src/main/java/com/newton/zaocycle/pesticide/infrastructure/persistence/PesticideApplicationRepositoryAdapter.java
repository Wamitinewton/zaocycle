package com.newton.zaocycle.pesticide.infrastructure.persistence;

import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.pesticide.domain.port.PesticideApplicationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
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
}
