package com.newton.zaocycle.collection.application.command;

import com.newton.zaocycle.collection.domain.model.PickupSource;

import java.time.LocalDate;
import java.util.UUID;

public record RequestPickupCommand(
        UUID farmerId,
        UUID applicationId,
        LocalDate scheduledFor,
        PickupSource source
) {
}
