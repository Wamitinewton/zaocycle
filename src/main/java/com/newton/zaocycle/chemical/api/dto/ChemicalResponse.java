package com.newton.zaocycle.chemical.api.dto;

import com.newton.zaocycle.chemical.domain.model.Chemical;
import com.newton.zaocycle.chemical.domain.model.ChemicalCategory;

import java.util.UUID;

public record ChemicalResponse(
        UUID id,
        String name,
        String activeIngredient,
        ChemicalCategory category,
        int halfLifeDays,
        int phiDays,
        String commonCrops
) {
    public static ChemicalResponse from(Chemical c) {
        return new ChemicalResponse(
                c.id(), c.name(), c.activeIngredient(),
                c.category(), c.halfLifeDays(), c.phiDays(), c.commonCrops()
        );
    }
}
