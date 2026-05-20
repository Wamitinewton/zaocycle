package com.newton.zaocycle.collection.domain.port;

import com.newton.zaocycle.collection.application.PickupFilter;
import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PickupRepository {
    Optional<WastePickup> findById(UUID id);

    WastePickup save(WastePickup pickup);

    List<WastePickup> findByRiderIdAndScheduledFor(UUID riderId, LocalDate date);

    List<WastePickup> findByRiderId(UUID riderId);

    List<WastePickup> findByFarmerId(UUID farmerId);

    boolean existsByFarmerIdAndStatusIn(UUID farmerId, List<PickupStatus> statuses);

    BigDecimal sumPayoutsByFarmer(UUID farmerId);

    BigDecimal sumPayoutsByFarmerSince(UUID farmerId, Instant since);

    long countPaidByFarmer(UUID farmerId);

    List<WastePickup> findRecentPaidByFarmer(UUID farmerId, int limit);

    Page<WastePickup> search(PickupFilter filter, Pageable pageable);
}
