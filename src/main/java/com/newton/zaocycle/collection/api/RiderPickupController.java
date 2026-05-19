package com.newton.zaocycle.collection.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.collection.api.dto.WastePickupResponse;
import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.collection.application.PickupService;
import com.newton.zaocycle.collection.application.command.CollectPickupCommand;
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

    public RiderPickupController(PickupService pickupService,
                                  PickupQueryService pickupQueryService) {
        this.pickupService = pickupService;
        this.pickupQueryService = pickupQueryService;
    }

    @GetMapping("/today")
    public List<WastePickupResponse> today(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return pickupQueryService.findForRiderToday(principal.id()).stream()
                .map(WastePickupResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public WastePickupResponse getById(@PathVariable UUID id) {
        return WastePickupResponse.from(pickupQueryService.findById(id));
    }

    @PostMapping("/{id}/collect")
    public WastePickupResponse collect(
            @PathVariable UUID id,
            @RequestParam BigDecimal weightKg,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) String notes) throws IOException {
        byte[] photoBytes = photo != null ? photo.getBytes() : null;
        CollectPickupCommand cmd = new CollectPickupCommand(weightKg, photoBytes, notes);
        return WastePickupResponse.from(pickupService.markCollected(id, cmd));
    }
}
