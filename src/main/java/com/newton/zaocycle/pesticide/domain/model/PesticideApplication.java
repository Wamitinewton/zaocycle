package com.newton.zaocycle.pesticide.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class PesticideApplication {

    private final UUID id;
    private final UUID farmerId;
    private final UUID chemicalId;
    private final String crop;
    private final Double quantityMl;
    private final Instant appliedAt;
    private final LocalDate safeHarvestDate;
    private ApplicationStatus status;
    private final String source;
    private final Instant createdAt;
    private Instant updatedAt;

    public PesticideApplication(UUID id, UUID farmerId, UUID chemicalId, String crop,
                                 Double quantityMl, Instant appliedAt, LocalDate safeHarvestDate,
                                 ApplicationStatus status, String source,
                                 Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.farmerId = farmerId;
        this.chemicalId = chemicalId;
        this.crop = crop;
        this.quantityMl = quantityMl;
        this.appliedAt = appliedAt;
        this.safeHarvestDate = safeHarvestDate;
        this.status = status;
        this.source = source;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void markSafe() {
        this.status = ApplicationStatus.SAFE;
        this.updatedAt = Instant.now();
    }

    public UUID id()                    { return id; }
    public UUID farmerId()              { return farmerId; }
    public UUID chemicalId()            { return chemicalId; }
    public String crop()                { return crop; }
    public Double quantityMl()          { return quantityMl; }
    public Instant appliedAt()          { return appliedAt; }
    public LocalDate safeHarvestDate()  { return safeHarvestDate; }
    public ApplicationStatus status()   { return status; }
    public String source()              { return source; }
    public Instant createdAt()          { return createdAt; }
    public Instant updatedAt()          { return updatedAt; }
}
