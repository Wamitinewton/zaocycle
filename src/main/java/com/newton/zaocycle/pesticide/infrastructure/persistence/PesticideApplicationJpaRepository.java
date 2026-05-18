package com.newton.zaocycle.pesticide.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface PesticideApplicationJpaRepository extends JpaRepository<PesticideApplicationEntity, UUID> {
    List<PesticideApplicationEntity> findByFarmerId(UUID farmerId);
    List<PesticideApplicationEntity> findByFarmerIdAndStatus(UUID farmerId, String status);
}
