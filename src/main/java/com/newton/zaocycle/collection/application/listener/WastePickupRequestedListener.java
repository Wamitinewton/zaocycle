package com.newton.zaocycle.collection.application.listener;

import com.newton.zaocycle.collection.application.PickupService;
import com.newton.zaocycle.collection.application.command.RequestPickupCommand;
import com.newton.zaocycle.collection.application.event.WastePickupRequested;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.notification.application.NotificationService;
import com.newton.zaocycle.scheduling.application.SchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@Component
public class WastePickupRequestedListener {

    private static final Logger log = LoggerFactory.getLogger(WastePickupRequestedListener.class);
    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    private final PickupService pickupService;
    private final SchedulingService schedulingService;
    private final FarmerService farmerService;
    private final NotificationService notifications;

    public WastePickupRequestedListener(PickupService pickupService,
                                        SchedulingService schedulingService,
                                        FarmerService farmerService,
                                        NotificationService notifications) {
        this.pickupService = pickupService;
        this.schedulingService = schedulingService;
        this.farmerService = farmerService;
        this.notifications = notifications;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(WastePickupRequested event) {
        LocalDate scheduledFor = LocalDate.now(NAIROBI).plusDays(1);
        WastePickup pickup = pickupService.requestPickup(new RequestPickupCommand(
                event.farmerId(), event.applicationId(), scheduledFor, event.source()));

        try {
            pickupService.autoAssign(pickup.id());
        } catch (Exception ex) {
            log.warn("Auto-assign failed for pickup {} (ward has no active riders): {}", pickup.id(), ex.getMessage());
            // Pickup is still saved; send confirmation and let ops assign manually
            notifyFarmerPendingAssignment(event.farmerId(), scheduledFor);
        }

        Instant reminderAt = scheduledFor.atTime(7, 0).atZone(NAIROBI).toInstant()
                .minus(Duration.ofHours(12));
        Instant expiryAt = Instant.now().plus(Duration.ofHours(48));

        schedulingService.schedulePickupReminder(pickup.id(), reminderAt);
        schedulingService.schedulePickupExpiry(pickup.id(), expiryAt);
    }

    private void notifyFarmerPendingAssignment(java.util.UUID farmerId, LocalDate scheduledFor) {
        farmerService.findById(farmerId).map(Farmer::phone).ifPresentOrElse(
                phone -> notifications.sendTemplated(phone.value(), "PICKUP_PENDING_ASSIGNMENT",
                        Map.of("date", scheduledFor.toString())),
                () -> log.warn("Could not notify farmer {}: phone not found", farmerId)
        );
    }
}
