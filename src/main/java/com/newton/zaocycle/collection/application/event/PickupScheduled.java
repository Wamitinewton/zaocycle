package com.newton.zaocycle.collection.application.event;

import java.time.LocalDate;
import java.util.UUID;

public record PickupScheduled(
        UUID pickupId,
        UUID farmerId,
        UUID riderId,
        LocalDate scheduledDate,
        String farmerPhone,
        String riderPhone,
        String farmerName,
        String riderName,
        String ward
) {}
