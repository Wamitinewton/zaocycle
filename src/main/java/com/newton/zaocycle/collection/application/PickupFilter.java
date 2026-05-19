package com.newton.zaocycle.collection.application;

import com.newton.zaocycle.collection.domain.model.PickupStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PickupFilter(
        PickupStatus status,
        UUID riderId,
        UUID farmerId,
        LocalDate fromDate,
        LocalDate toDate
) {}
