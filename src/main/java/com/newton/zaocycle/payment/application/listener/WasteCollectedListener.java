package com.newton.zaocycle.payment.application.listener;

import com.newton.zaocycle.collection.application.event.WasteCollected;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.payment.application.MpesaService;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WasteCollectedListener {

    private final MpesaService mpesaService;
    private final FarmerService farmerService;

    public WasteCollectedListener(MpesaService mpesaService, FarmerService farmerService) {
        this.mpesaService = mpesaService;
        this.farmerService = farmerService;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(WasteCollected event) {
        Farmer farmer = farmerService.findById(event.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer not found: " + event.farmerId()));
        mpesaService.initiateB2C(event.pickupId(), farmer.phone().value(), event.payoutAmount());
    }
}
