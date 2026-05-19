package com.newton.zaocycle.auth.infrastructure.persistence;

import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class StaffUserRepositoryAdapter implements StaffUserRepository {

    private final StaffUserJpaRepository jpa;

    StaffUserRepositoryAdapter(StaffUserJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<StaffUser> findByEmail(String email) {
        return jpa.findByEmail(email).map(StaffUserEntityMapper::toDomain);
    }

    @Override
    public Optional<StaffUser> findById(UUID id) {
        return jpa.findById(id).map(StaffUserEntityMapper::toDomain);
    }

    @Override
    public List<StaffUser> findAll() {
        return jpa.findAll().stream().map(StaffUserEntityMapper::toDomain).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpa.existsByEmail(email);
    }

    @Override
    public StaffUser save(StaffUser user) {
        StaffUserEntity entity = StaffUserEntityMapper.toEntity(user);
        return StaffUserEntityMapper.toDomain(jpa.save(entity));
    }
}
