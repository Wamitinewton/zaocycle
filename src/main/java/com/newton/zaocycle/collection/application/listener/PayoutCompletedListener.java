package com.newton.zaocycle.collection.application.listener;

import com.newton.zaocycle.collection.application.PickupService;
import com.newton.zaocycle.payment.application.event.PayoutCompleted;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component("collectionPayoutCompletedListener")
public class PayoutCompletedListener {

    private final PickupService pickupService;

    public PayoutCompletedListener(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(PayoutCompleted event) {
        pickupService.markPaid(event.pickupId(), event.mpesaTransactionId());
    }
}
