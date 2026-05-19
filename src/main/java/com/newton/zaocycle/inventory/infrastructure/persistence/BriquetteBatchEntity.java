package com.newton.zaocycle.inventory.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "briquette_batches")
@Getter
@Setter
@NoArgsConstructor
class BriquetteBatchEntity {

    @Id
    private UUID id;

    @Column(name = "batch_number", nullable = false, unique = true, length = 50)
    private String batchNumber;

    @Column(name = "kg_produced", nullable = false, precision = 10, scale = 2)
    private BigDecimal kgProduced;

    @Column(name = "kg_remaining", nullable = false, precision = 10, scale = 2)
    private BigDecimal kgRemaining;

    @Column(name = "produced_at", nullable = false)
    private Instant producedAt;

    @Column(name = "source_intake_id")
    private UUID sourceIntakeId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
