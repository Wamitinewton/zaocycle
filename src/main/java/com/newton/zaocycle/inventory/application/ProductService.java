package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    List<BriquetteProduct> listActive();
    BriquetteProduct findById(UUID id);
    BriquetteProduct deactivate(UUID id);
}
