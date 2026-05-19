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
        notifications.sendTemplated(event.farmerPhone(), "PICKUP_SCHEDULED_FARMER",
                Map.of("date", event.scheduledDate().toString()));
        notifications.sendTemplated(event.riderPhone(), "PICKUP_ASSIGNED_RIDER",
                Map.of("farmerName", event.farmerName(), "ward", event.ward(),
                        "date", event.scheduledDate().toString()));
    }
}
