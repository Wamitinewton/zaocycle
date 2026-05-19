package com.newton.zaocycle.inventory.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface BuyerOrderJpaRepository extends JpaRepository<BuyerOrderEntity, UUID> {

    Page<BuyerOrderEntity> findByBuyerId(UUID buyerId, Pageable pageable);

    Page<BuyerOrderEntity> findByStatus(String status, Pageable pageable);
}
