package com.newton.zaocycle.notification.application.listener;

import com.newton.zaocycle.collection.application.event.PickupScheduled;
import com.newton.zaocycle.notification.application.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class PickupScheduledListener {

    private final NotificationService notifications;

    public PickupScheduledListener(NotificationService notifications) {
        this.notifications = notifications;
    }

    @TransactionalEventListener
    public void on(PickupScheduled event) {
        if (event.farmerPhone() != null) {
            notifications.sendTemplated(event.farmerPhone(), "PICKUP_SCHEDULED_FARMER",
                    Map.of("date", event.scheduledDate().toString(),
                            "riderName", event.riderName() != null ? event.riderName() : "a rider"));
        }
        if (event.riderPhone() != null) {
            notifications.sendTemplated(event.riderPhone(), "PICKUP_ASSIGNED_RIDER",
                    Map.of("farmerName", event.farmerName() != null ? event.farmerName() : "Farmer",
                            "ward", event.ward() != null ? event.ward() : "",
                            "date", event.scheduledDate().toString()));
        }
    }
}
