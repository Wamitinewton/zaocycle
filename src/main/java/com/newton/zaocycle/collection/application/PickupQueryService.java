package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.domain.model.FarmerEarnings;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PickupQueryService {
    List<WastePickup> findForRiderToday(UUID riderId);

    List<WastePickup> findByRider(UUID riderId);

    List<WastePickup> findByFarmer(UUID farmerId);

    WastePickup findById(UUID id);

    FarmerEarnings getFarmerEarnings(UUID farmerId);

    boolean hasOpenPickup(UUID farmerId);

    Page<WastePickup> search(PickupFilter filter, Pageable pageable);
}
