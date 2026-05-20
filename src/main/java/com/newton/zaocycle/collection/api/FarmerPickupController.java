package com.newton.zaocycle.collection.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.collection.api.dto.FarmerEarningsResponse;
import com.newton.zaocycle.collection.api.dto.WastePickupResponse;
import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('FARMER')")
public class FarmerPickupController {

    private final PickupQueryService pickupQueryService;

    public FarmerPickupController(PickupQueryService pickupQueryService) {
        this.pickupQueryService = pickupQueryService;
    }

    @GetMapping("/api/v1/farmer/pickups")
    public ResponseEntity<ApiResponse<List<WastePickupResponse>>> myPickups(
            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        List<WastePickupResponse> body = pickupQueryService.findByFarmer(principal.id()).stream()
                .map(WastePickupResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/api/v1/farmer/earnings")
    public ResponseEntity<ApiResponse<FarmerEarningsResponse>> earnings(
            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(
                FarmerEarningsResponse.from(pickupQueryService.getFarmerEarnings(principal.id()))));
    }
}
