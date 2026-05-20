package com.newton.zaocycle.inventory.api;

import com.newton.zaocycle.inventory.api.dto.ProductResponse;
import com.newton.zaocycle.inventory.application.ProductService;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> listActive() {
        List<ProductResponse> body = productService.listActive().stream()
                .map(ProductResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ProductResponse.from(productService.findById(id))));
    }
}
