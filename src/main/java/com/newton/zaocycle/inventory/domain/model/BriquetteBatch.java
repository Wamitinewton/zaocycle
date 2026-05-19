package com.newton.zaocycle.inventory.domain.model;

import com.newton.zaocycle.shared.exception.ValidationException;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class BriquetteBatch {

    private final UUID id;
    private final String batchNumber;
    private final BigDecimal kgProduced;
    private BigDecimal kgRemaining;
    private final Instant producedAt;
    private final UUID sourceIntakeId;
    private final Instant createdAt;

    public BriquetteBatch(UUID id, String batchNumber, BigDecimal kgProduced,
                           BigDecimal kgRemaining, Instant producedAt,
                           UUID sourceIntakeId, Instant createdAt) {
        this.id = id;
        this.batchNumber = batchNumber;
        this.kgProduced = kgProduced;
        this.kgRemaining = kgRemaining;
        this.producedAt = producedAt;
        this.sourceIntakeId = sourceIntakeId;
        this.createdAt = createdAt;
    }

    public static BriquetteBatch create(String batchNumber, BigDecimal kgProduced,
                                         Instant producedAt, UUID sourceIntakeId) {
        return new BriquetteBatch(IdGenerator.generate(), batchNumber, kgProduced,
                kgProduced, producedAt, sourceIntakeId, Instant.now());
    }

    public void deduct(BigDecimal kg) {
        if (kg.compareTo(kgRemaining) > 0) {
            throw new ValidationException(
                    "Cannot deduct " + kg + " kg; only " + kgRemaining + " kg remaining in batch " + batchNumber);
        }
        this.kgRemaining = this.kgRemaining.subtract(kg);
    }

    public UUID id()                { return id; }
    public String batchNumber()     { return batchNumber; }
    public BigDecimal kgProduced()  { return kgProduced; }
    public BigDecimal kgRemaining() { return kgRemaining; }
    public Instant producedAt()     { return producedAt; }
    public UUID sourceIntakeId()    { return sourceIntakeId; }
    public Instant createdAt()      { return createdAt; }
}
