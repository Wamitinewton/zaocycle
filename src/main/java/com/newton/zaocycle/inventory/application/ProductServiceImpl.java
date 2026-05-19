package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;
import com.newton.zaocycle.inventory.domain.port.BriquetteProductRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
class ProductServiceImpl implements ProductService {

    private final BriquetteProductRepository productRepository;

    ProductServiceImpl(BriquetteProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<BriquetteProduct> listActive() {
        return productRepository.findAllActiveOrdered();
    }

    @Override
    public BriquetteProduct findById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    @Override
    @Transactional
    public BriquetteProduct deactivate(UUID id) {
        BriquetteProduct product = findById(id);
        product.deactivate();
        return productRepository.save(product);
    }
}
