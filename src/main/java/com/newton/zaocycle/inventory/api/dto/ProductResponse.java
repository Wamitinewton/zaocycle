package com.newton.zaocycle.inventory.api.dto;

import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        BigDecimal weightKg,
        BigDecimal unitPrice,
        String imageUrl,
        int sortOrder
) {
    public static ProductResponse from(BriquetteProduct p) {
        return new ProductResponse(p.id(), p.sku(), p.name(), p.description(),
                p.weightKg(), p.unitPrice(), p.imageUrl(), p.sortOrder());
    }
}
