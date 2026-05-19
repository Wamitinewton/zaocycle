package com.newton.zaocycle.notification.application.listener;

import com.newton.zaocycle.notification.application.NotificationService;
import com.newton.zaocycle.payment.application.event.PayoutCompleted;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component("notificationPayoutCompletedListener")
public class PayoutCompletedListener {

    private final NotificationService notifications;

    public PayoutCompletedListener(NotificationService notifications) {
        this.notifications = notifications;
    }

    @TransactionalEventListener
    public void on(PayoutCompleted event) {
        notifications.sendTemplated(event.farmerPhone(), "PAYOUT_CONFIRMATION",
                Map.of("amount", event.amount().toPlainString(),
                        "phone", event.farmerPhone(),
                        "kg", event.weightKg().toPlainString(),
                        "txId", event.mpesaReceiptNumber()));
    }
}
