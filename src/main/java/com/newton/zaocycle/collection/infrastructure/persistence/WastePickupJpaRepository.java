package com.newton.zaocycle.collection.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

interface WastePickupJpaRepository
        extends JpaRepository<WastePickupEntity, UUID>,
                JpaSpecificationExecutor<WastePickupEntity> {

    List<WastePickupEntity> findByRiderIdAndScheduledFor(UUID riderId, LocalDate date);

    List<WastePickupEntity> findByFarmerId(UUID farmerId);

    boolean existsByFarmerIdAndStatusIn(UUID farmerId, List<String> statuses);

    @Query("SELECT COALESCE(SUM(w.payoutAmount), 0) FROM WastePickupEntity w " +
           "WHERE w.farmerId = :farmerId AND w.status = 'PAID'")
    BigDecimal sumPayoutsByFarmer(@Param("farmerId") UUID farmerId);

    @Query("SELECT COALESCE(SUM(w.payoutAmount), 0) FROM WastePickupEntity w " +
           "WHERE w.farmerId = :farmerId AND w.status = 'PAID' AND w.paidAt >= :since")
    BigDecimal sumPayoutsByFarmerSince(@Param("farmerId") UUID farmerId, @Param("since") Instant since);

    @Query("SELECT COUNT(w) FROM WastePickupEntity w " +
           "WHERE w.farmerId = :farmerId AND w.status = 'PAID'")
    long countPaidByFarmer(@Param("farmerId") UUID farmerId);

    List<WastePickupEntity> findTop3ByFarmerIdAndStatusOrderByPaidAtDesc(UUID farmerId, String status);
}
