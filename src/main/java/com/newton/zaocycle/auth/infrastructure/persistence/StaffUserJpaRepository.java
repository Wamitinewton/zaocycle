package com.newton.zaocycle.auth.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface StaffUserJpaRepository extends JpaRepository<StaffUserEntity, UUID> {
    Optional<StaffUserEntity> findByEmail(String email);
}
