package com.newton.zaocycle.payment.application.listener;

import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.inventory.application.event.BuyerOrderPlaced;
import com.newton.zaocycle.payment.application.PaymentService;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderPlacedListener {

    private final PaymentService paymentService;
    private final BuyerService buyerService;

    public OrderPlacedListener(PaymentService paymentService, BuyerService buyerService) {
        this.paymentService = paymentService;
        this.buyerService = buyerService;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(BuyerOrderPlaced event) {
        Buyer buyer = buyerService.findById(event.buyerId())
                .orElseThrow(() -> new NotFoundException("Buyer not found: " + event.buyerId()));
        String reference = "ZC-" + event.orderId().toString().substring(0, 8).toUpperCase();
        paymentService.initiateStkPush(event.orderId(), buyer.phone(), event.totalAmount(), reference);
    }
}
