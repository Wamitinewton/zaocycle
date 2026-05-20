package com.newton.zaocycle.certification.api;

import com.newton.zaocycle.certification.api.dto.CertificateResponse;
import com.newton.zaocycle.certification.application.CertificateService;
import com.newton.zaocycle.certification.domain.model.Certificate;
import com.newton.zaocycle.shared.api.ApiResponse;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard/certificates")
@PreAuthorize("hasAnyRole('COOP_MANAGER','ADMIN')")
public class CertificateController {

    private final CertificateService certService;

    public CertificateController(CertificateService certService) {
        this.certService = certService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificateResponse>> getById(@PathVariable UUID id) {
        Certificate cert = certService.findById(id)
                .orElseThrow(() -> new NotFoundException("Certificate: " + id));
        return ResponseEntity.ok(ApiResponse.ok(CertificateResponse.from(cert)));
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<ApiResponse<CertificateResponse>> revoke(@PathVariable UUID id) {
        Certificate cert = certService.revoke(id);
        return ResponseEntity.ok(ApiResponse.ok(CertificateResponse.from(cert)));
    }
}
