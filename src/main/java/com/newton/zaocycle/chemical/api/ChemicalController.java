package com.newton.zaocycle.chemical.api;

import com.newton.zaocycle.chemical.api.dto.ChemicalResponse;
import com.newton.zaocycle.chemical.application.ChemicalService;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chemicals")
class ChemicalController {

    private final ChemicalService service;

    ChemicalController(ChemicalService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<ChemicalResponse>>> listActive() {
        List<ChemicalResponse> body = service.listAllActive().stream()
                .map(ChemicalResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ChemicalResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(ChemicalResponse.from(service.getById(id))));
    }
}
