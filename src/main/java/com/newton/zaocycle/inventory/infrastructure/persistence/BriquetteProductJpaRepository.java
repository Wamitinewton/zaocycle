package com.newton.zaocycle.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface BriquetteProductJpaRepository extends JpaRepository<BriquetteProductEntity, UUID> {

    List<BriquetteProductEntity> findAllByActiveTrueOrderBySortOrderAsc();
}
