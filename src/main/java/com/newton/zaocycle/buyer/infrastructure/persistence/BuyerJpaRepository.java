package com.newton.zaocycle.buyer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface BuyerJpaRepository extends JpaRepository<BuyerEntity, UUID> {
    Optional<BuyerEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
