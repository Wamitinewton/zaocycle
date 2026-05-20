package com.newton.zaocycle.collection.api;

import com.newton.zaocycle.collection.api.dto.WastePickupResponse;
import com.newton.zaocycle.collection.application.PickupFilter;
import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.collection.application.PickupService;
import com.newton.zaocycle.collection.domain.model.PickupStatus;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard/pickups")
@PreAuthorize("hasAnyRole('COOP_MANAGER','ADMIN')")
public class PickupController {

    private final PickupService pickupService;
    private final PickupQueryService pickupQueryService;

    public PickupController(PickupService pickupService,
                            PickupQueryService pickupQueryService) {
        this.pickupService = pickupService;
        this.pickupQueryService = pickupQueryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WastePickupResponse>>> search(
            @RequestParam(required = false) PickupStatus status,
            @RequestParam(required = false) UUID riderId,
            @RequestParam(required = false) UUID farmerId,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @PageableDefault(size = 20, sort = "scheduledFor") Pageable pageable) {
        PickupFilter filter = new PickupFilter(status, riderId, farmerId, fromDate, toDate);
        Page<WastePickupResponse> page = pickupQueryService.search(filter, pageable).map(WastePickupResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ApiResponse<WastePickupResponse>> assign(@PathVariable UUID id,
                                                                   @RequestParam UUID riderId) {
        return ResponseEntity.ok(ApiResponse.ok(WastePickupResponse.from(pickupService.assignRider(id, riderId))));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<WastePickupResponse>> cancel(@PathVariable UUID id) {
        pickupService.cancel(id);
        return ResponseEntity.ok(ApiResponse.ok(WastePickupResponse.from(pickupQueryService.findById(id))));
    }
}
