package com.newton.zaocycle.rider.api;

import com.newton.zaocycle.rider.api.dto.RegisterRiderRequest;
import com.newton.zaocycle.rider.api.dto.RiderResponse;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.rider.application.command.RegisterRiderCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/riders")
public class RiderController {

    private final RiderService riderService;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RiderResponse> register(@Valid @RequestBody RegisterRiderRequest request) {
        RegisterRiderCommand cmd = new RegisterRiderCommand(
                request.phone(), request.fullName(), request.ward(), request.password());
        RiderResponse response = RiderResponse.from(riderService.register(cmd));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COOP_MANAGER','ADMIN')")
    public ResponseEntity<RiderResponse> getById(@PathVariable UUID id) {
        return riderService.findById(id)
                .map(RiderResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RiderResponse> deactivate(@PathVariable UUID id) {
        return ResponseEntity.ok(RiderResponse.from(riderService.deactivate(id)));
    }
}
