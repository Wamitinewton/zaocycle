package com.newton.zaocycle.inventory.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.inventory.api.dto.RecordIntakeRequest;
import com.newton.zaocycle.inventory.application.IntakeService;
import com.newton.zaocycle.inventory.application.command.RecordIntakeCommand;
import com.newton.zaocycle.inventory.domain.model.WasteIntakeBatch;
import com.newton.zaocycle.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard/inventory/intake")
@PreAuthorize("hasAnyRole('COOP_MANAGER','ADMIN')")
public class IntakeController {

    private final IntakeService intakeService;

    public IntakeController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WasteIntakeBatch>> record(
            @Valid @RequestBody RecordIntakeRequest request,
            @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        RecordIntakeCommand cmd = new RecordIntakeCommand(
                request.intakeDate(), request.totalKg(),
                request.pickupIds(), request.notes(), principal.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(intakeService.record(cmd)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WasteIntakeBatch>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(intakeService.findAll()));
    }
}
