package com.newton.zaocycle.notification.application.listener;

import com.newton.zaocycle.payment.application.event.OrderPaid;
import com.newton.zaocycle.notification.application.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class OrderPaidListener {

    private final NotificationService notifications;

    public OrderPaidListener(NotificationService notifications) {
        this.notifications = notifications;
    }

    @TransactionalEventListener
    public void on(OrderPaid event) {
        notifications.sendTemplated(event.buyerPhone(), "ORDER_PAID_BUYER",
                Map.of("orderId", event.orderId().toString().substring(0, 8).toUpperCase(),
                        "kg", event.totalKg().toPlainString(),
                        "amount", event.totalAmount().toPlainString(),
                        "date", event.requestedDeliveryDate() != null
                                ? event.requestedDeliveryDate().toString() : "TBD"));
    }
}
