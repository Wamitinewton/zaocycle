package com.newton.zaocycle.auth.api;

import com.newton.zaocycle.auth.api.dto.MeResponse;
import com.newton.zaocycle.auth.api.dto.RefreshTokenRequest;
import com.newton.zaocycle.auth.api.dto.TokenResponse;
import com.newton.zaocycle.auth.api.dto.UnifiedLoginRequest;
import com.newton.zaocycle.auth.application.AuthService;
import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.buyer.api.dto.RegisterBuyerRequest;
import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.rider.api.dto.RegisterRiderRequest;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.rider.application.command.RegisterRiderCommand;
import com.newton.zaocycle.rider.domain.model.Rider;
import com.newton.zaocycle.shared.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final BuyerService buyerService;
    private final RiderService riderService;

    public AuthController(AuthService authService, BuyerService buyerService, RiderService riderService) {
        this.authService = authService;
        this.buyerService = buyerService;
        this.riderService = riderService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody UnifiedLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.loginUnified(request.identifier(), request.credential())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MeResponse>> me(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        MeResponse me = new MeResponse(
                principal.id().toString(),
                principal.role().name(),
                principal.displayName(),
                principal.phone(),
                principal.email()
        );
        return ResponseEntity.ok(ApiResponse.ok(me));
    }

    @PostMapping("/buyer/register")
    public ResponseEntity<ApiResponse<TokenResponse>> buyerRegister(@Valid @RequestBody RegisterBuyerRequest request) {
        Buyer buyer = buyerService.register(request.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(authService.issueTokensForBuyer(buyer)));
    }

    @PostMapping("/rider/register")
    public ResponseEntity<ApiResponse<TokenResponse>> riderRegister(@Valid @RequestBody RegisterRiderRequest request) {
        Rider rider = riderService.register(
                new RegisterRiderCommand(request.phone(), request.fullName(), request.ward(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(authService.issueTokensForRider(rider)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(request.refreshToken())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
