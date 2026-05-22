package com.newton.zaocycle.payment.application.listener;

import com.newton.zaocycle.collection.application.event.WasteCollected;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.payment.application.PaymentService;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WasteCollectedListener {

    private final PaymentService paymentService;
    private final FarmerService farmerService;

    public WasteCollectedListener(PaymentService paymentService, FarmerService farmerService) {
        this.paymentService = paymentService;
        this.farmerService = farmerService;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(WasteCollected event) {
        Farmer farmer = farmerService.findById(event.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer not found: " + event.farmerId()));
        paymentService.initiateB2C(event.pickupId(), farmer.phone().value(), event.payoutAmount());
    }
}
