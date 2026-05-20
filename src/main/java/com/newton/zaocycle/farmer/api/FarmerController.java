package com.newton.zaocycle.farmer.api;

import com.newton.zaocycle.farmer.api.dto.FarmerResponse;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.shared.api.ApiResponse;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/farmers")
class FarmerController {

    private final FarmerService service;

    FarmerController(FarmerService service) {
        this.service = service;
    }

    @GetMapping("/by-phone")
    ResponseEntity<ApiResponse<FarmerResponse>> getByPhone(@RequestParam String phone) {
        return service.findByPhone(PhoneNumber.of(phone))
                .map(FarmerResponse::from)
                .map(r -> ResponseEntity.ok(ApiResponse.ok(r)))
                .orElse(ResponseEntity.notFound().build());
    }
}
