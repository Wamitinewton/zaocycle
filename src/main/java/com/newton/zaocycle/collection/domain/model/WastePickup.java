package com.newton.zaocycle.collection.domain.model;

import com.newton.zaocycle.shared.exception.ValidationException;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class WastePickup {

    private final UUID id;
    private final UUID farmerId;
    private UUID riderId;
    private final UUID applicationId;
    private final Instant requestedAt;
    private final LocalDate scheduledFor;
    private PickupStatus status;
    private BigDecimal weightKg;
    private String photoUrl;
    private String notes;
    private BigDecimal payoutAmount;
    private UUID mpesaTransactionId;
    private Instant collectedAt;
    private Instant paidAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public WastePickup(UUID id, UUID farmerId, UUID riderId, UUID applicationId,
                       Instant requestedAt, LocalDate scheduledFor, PickupStatus status,
                       BigDecimal weightKg, String photoUrl, String notes,
                       BigDecimal payoutAmount, UUID mpesaTransactionId,
                       Instant collectedAt, Instant paidAt,
                       Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.farmerId = farmerId;
        this.riderId = riderId;
        this.applicationId = applicationId;
        this.requestedAt = requestedAt;
        this.scheduledFor = scheduledFor;
        this.status = status;
        this.weightKg = weightKg;
        this.photoUrl = photoUrl;
        this.notes = notes;
        this.payoutAmount = payoutAmount;
        this.mpesaTransactionId = mpesaTransactionId;
        this.collectedAt = collectedAt;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static WastePickup request(UUID farmerId, UUID applicationId, LocalDate scheduledFor) {
        Instant now = Instant.now();
        return new WastePickup(IdGenerator.generate(), farmerId, null, applicationId,
                now, scheduledFor, PickupStatus.REQUESTED,
                null, null, null, null, null, null, null, now, now);
    }

    public void assign(UUID riderId) {
        if (this.status != PickupStatus.REQUESTED) {
            throw new ValidationException("Pickup must be REQUESTED to assign a rider; current status: " + this.status);
        }
        this.riderId = riderId;
        this.status = PickupStatus.ASSIGNED;
        this.updatedAt = Instant.now();
    }

    public void markCollected(BigDecimal weightKg, String photoUrl, String notes, BigDecimal payoutAmount) {
        if (this.status != PickupStatus.ASSIGNED) {
            throw new ValidationException("Pickup must be ASSIGNED to mark collected; current status: " + this.status);
        }
        this.weightKg = weightKg;
        this.photoUrl = photoUrl;
        this.notes = notes;
        this.payoutAmount = payoutAmount;
        this.status = PickupStatus.COLLECTED;
        this.collectedAt = Instant.now();
        this.updatedAt = this.collectedAt;
    }

    public void markPaid(UUID mpesaTransactionId) {
        if (this.status != PickupStatus.COLLECTED) {
            throw new ValidationException("Pickup must be COLLECTED to mark paid; current status: " + this.status);
        }
        this.mpesaTransactionId = mpesaTransactionId;
        this.status = PickupStatus.PAID;
        this.paidAt = Instant.now();
        this.updatedAt = this.paidAt;
    }

    public void cancel() {
        if (this.status != PickupStatus.REQUESTED && this.status != PickupStatus.ASSIGNED) {
            throw new ValidationException("Pickup can only be cancelled when REQUESTED or ASSIGNED; current status: " + this.status);
        }
        this.status = PickupStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void fail() {
        this.status = PickupStatus.FAILED;
        this.updatedAt = Instant.now();
    }

    public UUID id()                    { return id; }
    public UUID farmerId()              { return farmerId; }
    public UUID riderId()               { return riderId; }
    public UUID applicationId()         { return applicationId; }
    public Instant requestedAt()        { return requestedAt; }
    public LocalDate scheduledFor()     { return scheduledFor; }
    public PickupStatus status()        { return status; }
    public BigDecimal weightKg()        { return weightKg; }
    public String photoUrl()            { return photoUrl; }
    public String notes()               { return notes; }
    public BigDecimal payoutAmount()    { return payoutAmount; }
    public UUID mpesaTransactionId()    { return mpesaTransactionId; }
    public Instant collectedAt()        { return collectedAt; }
    public Instant paidAt()             { return paidAt; }
    public Instant createdAt()          { return createdAt; }
    public Instant updatedAt()          { return updatedAt; }
}
