package com.newton.zaocycle.scheduling.application;

import java.time.Instant;
import java.util.UUID;

public interface SchedulingService {
    void scheduleHarvestMaturity(UUID applicationId, Instant runAt);

    void schedulePickupReminder(UUID pickupId, Instant runAt);

    void schedulePickupExpiry(UUID pickupId, Instant runAt);

    void cancelHarvestMaturity(UUID applicationId);

    void cancelPickupJobs(UUID pickupId);
}
