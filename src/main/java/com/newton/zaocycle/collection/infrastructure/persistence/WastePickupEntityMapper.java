package com.newton.zaocycle.collection.infrastructure.persistence;

import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.collection.domain.model.WastePickup;

final class WastePickupEntityMapper {

    private WastePickupEntityMapper() {
    }

    static WastePickup toDomain(WastePickupEntity e) {
        return new WastePickup(
                e.getId(), e.getFarmerId(), e.getRiderId(), e.getApplicationId(),
                e.getRequestedAt(), e.getScheduledFor(),
                PickupStatus.valueOf(e.getStatus()),
                e.getWeightKg(), e.getPhotoUrl(), e.getNotes(),
                e.getPayoutAmount(), e.getMpesaTransactionId(),
                e.getCollectedAt(), e.getPaidAt(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    static WastePickupEntity toEntity(WastePickup domain) {
        WastePickupEntity e = new WastePickupEntity();
        e.setId(domain.id());
        e.setFarmerId(domain.farmerId());
        e.setRiderId(domain.riderId());
        e.setApplicationId(domain.applicationId());
        e.setRequestedAt(domain.requestedAt());
        e.setScheduledFor(domain.scheduledFor());
        e.setStatus(domain.status().name());
        e.setWeightKg(domain.weightKg());
        e.setPhotoUrl(domain.photoUrl());
        e.setNotes(domain.notes());
        e.setPayoutAmount(domain.payoutAmount());
        e.setMpesaTransactionId(domain.mpesaTransactionId());
        e.setCollectedAt(domain.collectedAt());
        e.setPaidAt(domain.paidAt());
        return e;
    }
}
