package com.newton.zaocycle.collection.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "waste_pickups")
@Getter
@Setter
@NoArgsConstructor
class WastePickupEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "rider_id")
    private UUID riderId;

    @Column(name = "application_id")
    private UUID applicationId;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "scheduled_for", nullable = false)
    private LocalDate scheduledFor;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column
    private String notes;

    @Column(name = "payout_amount", precision = 10, scale = 2)
    private BigDecimal payoutAmount;

    @Column(name = "mpesa_transaction_id")
    private UUID mpesaTransactionId;

    @Column(name = "collected_at")
    private Instant collectedAt;

    @Column(name = "paid_at")
    private Instant paidAt;
}
