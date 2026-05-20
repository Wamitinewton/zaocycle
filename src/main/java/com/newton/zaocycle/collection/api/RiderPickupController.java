package com.newton.zaocycle.collection.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.collection.api.dto.RiderPickupDetailResponse;
import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.collection.application.PickupService;
import com.newton.zaocycle.collection.application.command.CollectPickupCommand;
import com.newton.zaocycle.collection.domain.model.WastePickup;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rider/pickups")
@PreAuthorize("hasRole('RIDER')")
public class RiderPickupController {

    private final PickupService pickupService;
    private final PickupQueryService pickupQueryService;
    private final FarmerService farmerService;

    public RiderPickupController(PickupService pickupService,
                                 PickupQueryService pickupQueryService,
                                 FarmerService farmerService) {
        this.pickupService = pickupService;
        this.pickupQueryService = pickupQueryService;
        this.farmerService = farmerService;
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<RiderPickupDetailResponse>>> today(
            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        List<RiderPickupDetailResponse> body = pickupQueryService.findForRiderToday(principal.id()).stream()
                .map(this::enrich)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<RiderPickupDetailResponse>>> history(
            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        List<RiderPickupDetailResponse> body = pickupQueryService.findByRider(principal.id()).stream()
                .map(this::enrich)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiderPickupDetailResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(enrich(pickupQueryService.findById(id))));
    }

    @PostMapping("/{id}/collect")
    public ResponseEntity<ApiResponse<RiderPickupDetailResponse>> collect(
            @PathVariable UUID id,
            @RequestParam BigDecimal weightKg,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) String notes) throws IOException {
        byte[] photoBytes = photo != null ? photo.getBytes() : null;
        CollectPickupCommand cmd = new CollectPickupCommand(weightKg, photoBytes, notes);
        return ResponseEntity.ok(ApiResponse.ok(enrich(pickupService.markCollected(id, cmd))));
    }

    private RiderPickupDetailResponse enrich(WastePickup pickup) {
        Farmer farmer = farmerService.findById(pickup.farmerId()).orElse(null);
        return RiderPickupDetailResponse.from(pickup, farmer);
    }
}
