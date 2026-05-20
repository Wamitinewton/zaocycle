package com.newton.zaocycle.rider.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface RiderJpaRepository extends JpaRepository<RiderEntity, UUID> {
    Optional<RiderEntity> findByPhone(String phone);

    List<RiderEntity> findByAssignedWardAndActiveTrueOrderByCreatedAtAsc(String ward);
}
