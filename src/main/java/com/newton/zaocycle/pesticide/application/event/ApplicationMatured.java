package com.newton.zaocycle.pesticide.application.event;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ApplicationMatured(
        UUID applicationId,
        UUID farmerId,
        String crop,
        UUID chemicalId,
        Instant appliedAt,
        LocalDate safeHarvestDate
) {
}
