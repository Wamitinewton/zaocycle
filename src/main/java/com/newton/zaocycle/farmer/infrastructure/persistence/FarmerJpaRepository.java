package com.newton.zaocycle.farmer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface FarmerJpaRepository extends JpaRepository<FarmerEntity, UUID> {
    Optional<FarmerEntity> findByPhone(String phone);
}
