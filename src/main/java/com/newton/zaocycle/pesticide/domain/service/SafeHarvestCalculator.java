package com.newton.zaocycle.pesticide.domain.service;

import com.newton.zaocycle.chemical.domain.model.Chemical;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

public final class SafeHarvestCalculator {

    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    private final Clock clock;

    public SafeHarvestCalculator(Clock clock) {
        this.clock = clock;
    }

    public LocalDate computeSafeHarvestDate(Chemical chemical) {
        LocalDate today = LocalDate.now(clock.withZone(NAIROBI));
        int waitDays = Math.max(chemical.halfLifeDays(), chemical.phiDays());
        return today.plusDays(waitDays);
    }
}
