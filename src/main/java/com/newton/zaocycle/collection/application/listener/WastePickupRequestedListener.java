package com.newton.zaocycle.collection.application.listener;

import com.newton.zaocycle.collection.application.PickupService;
import com.newton.zaocycle.collection.application.command.RequestPickupCommand;
import com.newton.zaocycle.collection.application.event.WastePickupRequested;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.scheduling.application.SchedulingService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class WastePickupRequestedListener {

    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    private final PickupService pickupService;
    private final SchedulingService schedulingService;

    public WastePickupRequestedListener(PickupService pickupService,
                                        SchedulingService schedulingService) {
        this.pickupService = pickupService;
        this.schedulingService = schedulingService;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(WastePickupRequested event) {
        LocalDate scheduledFor = LocalDate.now(NAIROBI).plusDays(1);
        WastePickup pickup = pickupService.requestPickup(new RequestPickupCommand(
                event.farmerId(), event.applicationId(), scheduledFor, event.source()));

        pickupService.autoAssign(pickup.id());

        Instant reminderAt = scheduledFor.atTime(7, 0).atZone(NAIROBI).toInstant()
                .minus(Duration.ofHours(12));
        Instant expiryAt = Instant.now().plus(Duration.ofHours(48));

        schedulingService.schedulePickupReminder(pickup.id(), reminderAt);
        schedulingService.schedulePickupExpiry(pickup.id(), expiryAt);
    }
}
