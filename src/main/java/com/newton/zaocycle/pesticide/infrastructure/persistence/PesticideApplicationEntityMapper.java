package com.newton.zaocycle.pesticide.infrastructure.persistence;

import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;

import java.math.BigDecimal;

final class PesticideApplicationEntityMapper {

    private PesticideApplicationEntityMapper() {
    }

    static PesticideApplication toDomain(PesticideApplicationEntity e) {
        Double qty = (e.getQuantityMl() != null) ? e.getQuantityMl().doubleValue() : null;
        return new PesticideApplication(
                e.getId(), e.getFarmerId(), e.getChemicalId(),
                e.getCrop(), qty, e.getAppliedAt(), e.getSafeHarvestDate(),
                ApplicationStatus.valueOf(e.getStatus()), e.getSource(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    static PesticideApplicationEntity toEntity(PesticideApplication domain) {
        PesticideApplicationEntity e = new PesticideApplicationEntity();
        e.setId(domain.id());
        e.setFarmerId(domain.farmerId());
        e.setChemicalId(domain.chemicalId());
        e.setCrop(domain.crop());
        if (domain.quantityMl() != null) {
            e.setQuantityMl(BigDecimal.valueOf(domain.quantityMl()));
        }
        e.setAppliedAt(domain.appliedAt());
        e.setSafeHarvestDate(domain.safeHarvestDate());
        e.setStatus(domain.status().name());
        e.setSource(domain.source());
        return e;
    }
}
