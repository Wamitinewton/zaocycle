package com.newton.zaocycle.rider.infrastructure.persistence;

import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.rider.domain.port.RiderRepository;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class RiderRepositoryAdapter implements RiderRepository {

    private final RiderJpaRepository jpa;

    RiderRepositoryAdapter(RiderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Rider> findById(UUID id) {
        return jpa.findById(id).map(RiderEntityMapper::toDomain);
    }

    @Override
    public Optional<Rider> findByPhone(PhoneNumber phone) {
        return jpa.findByPhone(phone.value()).map(RiderEntityMapper::toDomain);
    }

    @Override
    public List<Rider> findActiveByWard(Ward ward) {
        return jpa.findByAssignedWardAndActiveTrueOrderByCreatedAtAsc(ward.name())
                .stream()
                .map(RiderEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Rider save(Rider rider) {
        return RiderEntityMapper.toDomain(jpa.save(RiderEntityMapper.toEntity(rider)));
    }
}
