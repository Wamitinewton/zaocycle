package com.newton.zaocycle.farmer.infrastructure.persistence;

import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.farmer.domain.port.FarmerRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class FarmerRepositoryAdapter implements FarmerRepository {

    private final FarmerJpaRepository jpa;

    FarmerRepositoryAdapter(FarmerJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Farmer> findByPhone(PhoneNumber phone) {
        return jpa.findByPhone(phone.value()).map(FarmerEntityMapper::toDomain);
    }

    @Override
    public Optional<Farmer> findById(UUID id) {
        return jpa.findById(id).map(FarmerEntityMapper::toDomain);
    }

    @Override
    public Farmer save(Farmer farmer) {
        return FarmerEntityMapper.toDomain(jpa.save(FarmerEntityMapper.toEntity(farmer)));
    }
}
