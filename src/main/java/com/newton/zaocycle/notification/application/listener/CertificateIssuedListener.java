package com.newton.zaocycle.notification.application.listener;

import com.newton.zaocycle.certification.application.event.CertificateIssued;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.notification.application.NotificationService;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Component
public class CertificateIssuedListener {

    private final FarmerService farmerService;
    private final NotificationService notifications;

    public CertificateIssuedListener(FarmerService farmerService, NotificationService notifications) {
        this.farmerService = farmerService;
        this.notifications = notifications;
    }

    @TransactionalEventListener
    public void on(CertificateIssued event) {
        Farmer farmer = farmerService.findById(event.farmerId())
                .orElseThrow(() -> new NotFoundException("Farmer: " + event.farmerId()));
        notifications.sendTemplated(farmer.phone().value(), "SAFE_HARVEST_NOTICE",
                Map.of("name", farmer.fullName(), "crop", event.crop()));
    }
}
