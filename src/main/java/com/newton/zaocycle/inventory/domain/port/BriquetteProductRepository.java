package com.newton.zaocycle.inventory.domain.port;

import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BriquetteProductRepository {
    List<BriquetteProduct> findAllActiveOrdered();
    Optional<BriquetteProduct> findById(UUID id);
    BriquetteProduct save(BriquetteProduct product);
}
