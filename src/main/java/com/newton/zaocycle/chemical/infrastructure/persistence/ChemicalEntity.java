package com.newton.zaocycle.chemical.infrastructure.persistence;

import com.newton.zaocycle.chemical.domain.model.ChemicalCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chemicals")
@Getter
@Setter
@NoArgsConstructor
class ChemicalEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "active_ingredient")
    private String activeIngredient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ChemicalCategory category;

    @Column(name = "half_life_days", nullable = false)
    private int halfLifeDays;

    @Column(name = "phi_days", nullable = false)
    private int phiDays;

    @Column(name = "common_crops", length = 500)
    private String commonCrops;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
