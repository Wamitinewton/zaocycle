package com.newton.zaocycle.notification.application.listener;

import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.notification.application.NotificationService;
import com.newton.zaocycle.scheduling.application.event.PickupReminderDue;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PickupReminderListener {

    private final PickupQueryService pickupQueryService;
    private final FarmerService farmerService;
    private final NotificationService notifications;

    public PickupReminderListener(PickupQueryService pickupQueryService,
                                  FarmerService farmerService,
                                  NotificationService notifications) {
        this.pickupQueryService = pickupQueryService;
        this.farmerService = farmerService;
        this.notifications = notifications;
    }

    @EventListener
    public void on(PickupReminderDue event) {
        WastePickup pickup = pickupQueryService.findById(event.pickupId());
        Farmer farmer = farmerService.findById(pickup.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer: " + pickup.farmerId()));
        notifications.sendTemplated(farmer.phone().value(), "PICKUP_REMINDER_FARMER",
                Map.of("date", pickup.scheduledFor().toString()));
    }
}
