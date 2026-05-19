package com.newton.zaocycle.auth.infrastructure.persistence;

import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public StaffUser save(StaffUser user) {
        StaffUserEntity entity = StaffUserEntityMapper.toEntity(user);
        return StaffUserEntityMapper.toDomain(jpa.save(entity));
    }
}
