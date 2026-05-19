package com.newton.zaocycle.scheduling.application.listener;

import com.newton.zaocycle.pesticide.application.event.PesticideApplicationLogged;
import com.newton.zaocycle.scheduling.application.SchedulingService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class PesticideApplicationLoggedListener {

    private final SchedulingService schedulingService;

    public PesticideApplicationLoggedListener(SchedulingService schedulingService) {
        this.schedulingService = schedulingService;
    }

    @TransactionalEventListener
    public void on(PesticideApplicationLogged event) {
        Instant runAt = event.safeHarvestDate()
                .atTime(6, 0)
                .atZone(ZoneId.of("Africa/Nairobi"))
                .toInstant();
        schedulingService.scheduleHarvestMaturity(event.applicationId(), runAt);
    }
}
