package com.newton.zaocycle.chemical.infrastructure.persistence;

import com.newton.zaocycle.chemical.domain.model.Chemical;

final class ChemicalEntityMapper {

    private ChemicalEntityMapper() {
    }

    static Chemical toDomain(ChemicalEntity entity) {
        return new Chemical(
                entity.getId(),
                entity.getName(),
                entity.getActiveIngredient(),
                entity.getCategory(),
                entity.getHalfLifeDays(),
                entity.getPhiDays(),
                entity.getCommonCrops(),
                entity.isActive()
        );
    }
}
