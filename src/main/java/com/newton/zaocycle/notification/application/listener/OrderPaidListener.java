package com.newton.zaocycle.notification.application.listener;

import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.notification.application.NotificationService;
import com.newton.zaocycle.payment.application.event.OrderPaid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class OrderPaidListener {

    private final NotificationService notifications;
    private final BuyerService buyerService;
    private final String opsPhone;

    public OrderPaidListener(NotificationService notifications,
                             BuyerService buyerService,
                             @Value("${zaocycle.notification.ops-phone}") String opsPhone) {
        this.notifications = notifications;
        this.buyerService = buyerService;
        this.opsPhone = opsPhone;
    }

    @TransactionalEventListener
    public void on(OrderPaid event) {
        String orderId = event.orderId().toString().substring(0, 8).toUpperCase();
        String kg = event.totalKg().toPlainString();
        String amount = event.totalAmount().toPlainString();
        String date = event.requestedDeliveryDate() != null
                ? event.requestedDeliveryDate().toString() : "TBD";

        notifications.sendTemplated(event.buyerPhone(), "ORDER_PAID_BUYER",
                Map.of("orderId", orderId, "kg", kg, "amount", amount, "date", date));

        String buyerName = buyerService.findById(event.buyerId())
                .map(b -> b.displayName())
                .orElse("Unknown buyer");
        notifications.sendTemplated(opsPhone, "ORDER_PAID_OPS",
                Map.of("kg", kg, "buyerName", buyerName, "orderId", orderId));
    }
}
