package com.newton.zaocycle.collection.application.event;

import com.newton.zaocycle.collection.domain.model.PickupSource;

import java.util.UUID;

public record WastePickupRequested(
        UUID farmerId,
        UUID applicationId,
        PickupSource source
) {
}
