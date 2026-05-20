package com.newton.zaocycle.pesticide.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pesticide_applications")
@Getter
@Setter
@NoArgsConstructor
class PesticideApplicationEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "farmer_id", nullable = false)
    private UUID farmerId;

    @Column(name = "chemical_id", nullable = false)
    private UUID chemicalId;

    @Column(nullable = false, length = 100)
    private String crop;

    @Column(name = "quantity_ml", precision = 10, scale = 2)
    private BigDecimal quantityMl;

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;

    @Column(name = "safe_harvest_date", nullable = false)
    private LocalDate safeHarvestDate;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false, length = 20)
    private String source;
}
