package com.newton.zaocycle.auth.api;

import com.newton.zaocycle.auth.api.dto.CreateStaffRequest;
import com.newton.zaocycle.auth.api.dto.StaffResponse;
import com.newton.zaocycle.auth.application.StaffService;
import com.newton.zaocycle.shared.api.ApiResponse;
import com.newton.zaocycle.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/staff")
public class StaffAdminController {

    private final StaffService staffService;

    public StaffAdminController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> create(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(StaffResponse.from(staffService.create(request.toCommand()))));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> listAll() {
        List<StaffResponse> body = staffService.findAll().stream().map(StaffResponse::from).toList();
        return ResponseEntity.ok(ApiResponse.ok(body));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOP_MANAGER')")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(@PathVariable UUID id) {
        StaffResponse response = staffService.findById(id)
                .map(StaffResponse::from)
                .orElseThrow(() -> new NotFoundException("Staff user not found: " + id));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(StaffResponse.from(staffService.deactivate(id))));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponse>> reactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(StaffResponse.from(staffService.reactivate(id))));
    }
}
