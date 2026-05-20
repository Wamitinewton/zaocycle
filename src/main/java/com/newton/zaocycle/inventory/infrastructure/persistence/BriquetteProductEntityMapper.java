package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;

final class BriquetteProductEntityMapper {

    private BriquetteProductEntityMapper() {
    }

    static BriquetteProduct toDomain(BriquetteProductEntity e) {
        return new BriquetteProduct(
                e.getId(), e.getSku(), e.getName(), e.getDescription(),
                e.getWeightKg(), e.getUnitPrice(), e.getImageUrl(),
                e.isActive(), e.getSortOrder(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    static BriquetteProductEntity toEntity(BriquetteProduct domain) {
        BriquetteProductEntity e = new BriquetteProductEntity();
        e.setId(domain.id());
        e.setSku(domain.sku());
        e.setName(domain.name());
        e.setDescription(domain.description());
        e.setWeightKg(domain.weightKg());
        e.setUnitPrice(domain.unitPrice());
        e.setImageUrl(domain.imageUrl());
        e.setActive(domain.isActive());
        e.setSortOrder(domain.sortOrder());
        return e;
    }
}
