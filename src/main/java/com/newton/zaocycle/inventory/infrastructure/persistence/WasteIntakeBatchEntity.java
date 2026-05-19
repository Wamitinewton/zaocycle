package com.newton.zaocycle.inventory.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "waste_intake_batches")
@Getter
@Setter
@NoArgsConstructor
class WasteIntakeBatchEntity {

    @Id
    private UUID id;

    @Column(name = "intake_date", nullable = false)
    private LocalDate intakeDate;

    @Column(name = "total_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalKg;

    @Column(name = "pickup_ids", nullable = false, columnDefinition = "jsonb")
    private String pickupIds;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "recorded_by")
    private UUID recordedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
