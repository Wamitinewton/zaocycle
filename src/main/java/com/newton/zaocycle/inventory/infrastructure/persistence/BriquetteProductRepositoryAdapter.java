package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;
import com.newton.zaocycle.inventory.domain.port.BriquetteProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class BriquetteProductRepositoryAdapter implements BriquetteProductRepository {

    private final BriquetteProductJpaRepository jpa;

    BriquetteProductRepositoryAdapter(BriquetteProductJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<BriquetteProduct> findAllActiveOrdered() {
        return jpa.findAllByActiveTrueOrderBySortOrderAsc().stream()
                .map(BriquetteProductEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BriquetteProduct> findById(UUID id) {
        return jpa.findById(id).map(BriquetteProductEntityMapper::toDomain);
    }

    @Override
    public BriquetteProduct save(BriquetteProduct product) {
        BriquetteProductEntity entity = BriquetteProductEntityMapper.toEntity(product);
        return BriquetteProductEntityMapper.toDomain(jpa.save(entity));
    }
}
