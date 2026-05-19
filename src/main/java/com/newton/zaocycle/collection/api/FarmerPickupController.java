package com.newton.zaocycle.collection.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.collection.api.dto.FarmerEarningsResponse;
import com.newton.zaocycle.collection.api.dto.WastePickupResponse;
import com.newton.zaocycle.collection.application.PickupQueryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('FARMER')")
public class FarmerPickupController {

    private final PickupQueryService pickupQueryService;

    public FarmerPickupController(PickupQueryService pickupQueryService) {
        this.pickupQueryService = pickupQueryService;
    }

    @GetMapping("/api/v1/farmer/pickups")
    public List<WastePickupResponse> myPickups(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return pickupQueryService.findByFarmer(principal.id()).stream()
                .map(WastePickupResponse::from)
                .toList();
    }

    @GetMapping("/api/v1/farmer/earnings")
    public FarmerEarningsResponse earnings(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return FarmerEarningsResponse.from(pickupQueryService.getFarmerEarnings(principal.id()));
    }
}
