package com.newton.zaocycle.inventory.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class WasteIntakeBatch {

    private final UUID id;
    private final LocalDate intakeDate;
    private final BigDecimal totalKg;
    private final List<UUID> pickupIds;
    private final String notes;
    private final UUID recordedBy;
    private final Instant createdAt;

    public WasteIntakeBatch(UUID id, LocalDate intakeDate, BigDecimal totalKg,
                             List<UUID> pickupIds, String notes, UUID recordedBy, Instant createdAt) {
        this.id = id;
        this.intakeDate = intakeDate;
        this.totalKg = totalKg;
        this.pickupIds = pickupIds;
        this.notes = notes;
        this.recordedBy = recordedBy;
        this.createdAt = createdAt;
    }

    public static WasteIntakeBatch create(LocalDate intakeDate, BigDecimal totalKg,
                                           List<UUID> pickupIds, String notes, UUID recordedBy) {
        return new WasteIntakeBatch(IdGenerator.generate(), intakeDate, totalKg,
                pickupIds, notes, recordedBy, Instant.now());
    }

    public UUID id()              { return id; }
    public LocalDate intakeDate() { return intakeDate; }
    public BigDecimal totalKg()   { return totalKg; }
    public List<UUID> pickupIds() { return pickupIds; }
    public String notes()         { return notes; }
    public UUID recordedBy()      { return recordedBy; }
    public Instant createdAt()    { return createdAt; }
}
