package com.newton.zaocycle.auth.api;

import com.newton.zaocycle.auth.api.dto.CreateStaffRequest;
import com.newton.zaocycle.auth.api.dto.StaffResponse;
import com.newton.zaocycle.auth.application.StaffService;
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
    public ResponseEntity<StaffResponse> create(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StaffResponse.from(staffService.create(request.toCommand())));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StaffResponse> listAll() {
        return staffService.findAll().stream().map(StaffResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COOP_MANAGER')")
    public StaffResponse getById(@PathVariable UUID id) {
        return staffService.findById(id)
                .map(StaffResponse::from)
                .orElseThrow(() -> new NotFoundException("Staff user not found: " + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public StaffResponse deactivate(@PathVariable UUID id) {
        return StaffResponse.from(staffService.deactivate(id));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public StaffResponse reactivate(@PathVariable UUID id) {
        return StaffResponse.from(staffService.reactivate(id));
    }
}
