package com.newton.zaocycle.chemical.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

interface ChemicalJpaRepository extends JpaRepository<ChemicalEntity, UUID> {

    List<ChemicalEntity> findByActiveTrue();

    @Query("SELECT c FROM ChemicalEntity c WHERE c.active = true AND c.commonCrops LIKE %:crop%")
    List<ChemicalEntity> findActiveByCrop(@Param("crop") String crop);
}
