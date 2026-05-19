package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.application.command.CollectPickupCommand;
import com.newton.zaocycle.collection.application.command.RequestPickupCommand;
import com.newton.zaocycle.collection.domain.model.WastePickup;

import java.util.UUID;

public interface PickupService {
    WastePickup requestPickup(RequestPickupCommand cmd);
    WastePickup assignRider(UUID pickupId, UUID riderId);
    WastePickup autoAssign(UUID pickupId);
    WastePickup markCollected(UUID pickupId, CollectPickupCommand cmd);
    WastePickup markPaid(UUID pickupId, UUID mpesaTxId);
    void reassignExpired(UUID pickupId);
    void cancel(UUID pickupId);
}
