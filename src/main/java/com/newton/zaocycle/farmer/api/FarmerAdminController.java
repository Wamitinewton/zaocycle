package com.newton.zaocycle.farmer.api;

import com.newton.zaocycle.farmer.api.dto.FarmerResponse;
import com.newton.zaocycle.farmer.api.dto.UpdateFarmerLocationRequest;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/farmers")
class FarmerAdminController {

    private final FarmerService farmerService;

    FarmerAdminController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @PatchMapping("/{id}/location")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOP_MANAGER')")
    ResponseEntity<ApiResponse<FarmerResponse>> updateLocation(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFarmerLocationRequest request) {
        FarmerResponse response = FarmerResponse.from(farmerService.updateLocation(request.toCommand(id)));
        return ResponseEntity.ok(ApiResponse.ok(response, "Location updated successfully"));
    }
}
