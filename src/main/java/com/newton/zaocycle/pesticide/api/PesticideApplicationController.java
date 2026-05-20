package com.newton.zaocycle.pesticide.api;

import com.newton.zaocycle.pesticide.api.dto.PesticideApplicationResponse;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pesticide-applications")
class PesticideApplicationController {

    private final PesticideApplicationService service;

    PesticideApplicationController(PesticideApplicationService service) {
        this.service = service;
    }

    @GetMapping("/farmer/{farmerId}")
    ResponseEntity<ApiResponse<List<PesticideApplicationResponse>>> getByFarmer(
            @PathVariable UUID farmerId) {
        List<PesticideApplicationResponse> body = service.getByFarmer(farmerId).stream()
                .map(PesticideApplicationResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }
}
