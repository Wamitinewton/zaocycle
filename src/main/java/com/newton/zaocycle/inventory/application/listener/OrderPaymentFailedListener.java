package com.newton.zaocycle.inventory.application.listener;

import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.port.BuyerOrderRepository;
import com.newton.zaocycle.payment.application.event.OrderPaymentFailed;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderPaymentFailedListener {

    private final BuyerOrderRepository orderRepository;

    public OrderPaymentFailedListener(BuyerOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(OrderPaymentFailed event) {
        BuyerOrder order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + event.orderId()));
        order.cancel();
        orderRepository.save(order);
    }
}
