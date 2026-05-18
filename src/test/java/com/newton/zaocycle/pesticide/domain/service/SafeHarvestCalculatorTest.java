package com.newton.zaocycle.pesticide.domain.service;

import com.newton.zaocycle.chemical.domain.model.Chemical;
import com.newton.zaocycle.chemical.domain.model.ChemicalCategory;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SafeHarvestCalculatorTest {

    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    private Chemical chemical(int halfLife, int phi) {
        return new Chemical(UUID.randomUUID(), "Test", null,
                ChemicalCategory.FUNGICIDE, halfLife, phi, "Tomatoes", true);
    }

    @Test
    void usesHalfLifeWhenGreater() {
        Clock fixed = Clock.fixed(Instant.parse("2026-05-18T06:00:00Z"), NAIROBI);
        SafeHarvestCalculator calc = new SafeHarvestCalculator(fixed);
        LocalDate result = calc.computeSafeHarvestDate(chemical(14, 7));
        assertEquals(LocalDate.of(2026, 6, 1), result);
    }

    @Test
    void usesPhiWhenGreater() {
        Clock fixed = Clock.fixed(Instant.parse("2026-05-18T06:00:00Z"), NAIROBI);
        SafeHarvestCalculator calc = new SafeHarvestCalculator(fixed);
        LocalDate result = calc.computeSafeHarvestDate(chemical(7, 21));
        assertEquals(LocalDate.of(2026, 6, 8), result);
    }

    @Test
    void usesEitherWhenEqual() {
        Clock fixed = Clock.fixed(Instant.parse("2026-05-18T06:00:00Z"), NAIROBI);
        SafeHarvestCalculator calc = new SafeHarvestCalculator(fixed);
        LocalDate result = calc.computeSafeHarvestDate(chemical(7, 7));
        assertEquals(LocalDate.of(2026, 5, 25), result);
    }

    @Test
    void computesFromNairobiTimezone() {
        // 23:00 UTC on May 17 = 02:00 EAT on May 18 — must use EAT date
        Clock fixed = Clock.fixed(Instant.parse("2026-05-17T23:00:00Z"), NAIROBI);
        SafeHarvestCalculator calc = new SafeHarvestCalculator(fixed);
        LocalDate result = calc.computeSafeHarvestDate(chemical(7, 7));
        assertEquals(LocalDate.of(2026, 5, 25), result);
    }
}
