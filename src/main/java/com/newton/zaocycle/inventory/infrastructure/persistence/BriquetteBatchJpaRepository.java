package com.newton.zaocycle.inventory.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

interface BriquetteBatchJpaRepository extends JpaRepository<BriquetteBatchEntity, UUID> {

    @Query("SELECT b FROM BriquetteBatchEntity b WHERE b.kgRemaining > 0 ORDER BY b.producedAt ASC")
    List<BriquetteBatchEntity> findWithRemainingStockOrderedByOldest();

    @Query("SELECT COALESCE(SUM(b.kgRemaining), 0) FROM BriquetteBatchEntity b WHERE b.kgRemaining > 0")
    BigDecimal sumRemaining();
}
