package com.newton.zaocycle.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface WasteIntakeBatchJpaRepository extends JpaRepository<WasteIntakeBatchEntity, UUID> {
}
