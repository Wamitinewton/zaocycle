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
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final BuyerService buyerService;

    public AuthController(AuthService authService, BuyerService buyerService) {
        this.authService = authService;
        this.buyerService = buyerService;
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody UnifiedLoginRequest request) {
        return authService.loginUnified(request.identifier(), request.credential());
    }

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return new MeResponse(
                principal.id().toString(),
                principal.role().name(),
                principal.displayName(),
                principal.phone(),
                principal.email()
        );
    }

    @PostMapping("/buyer/register")
    public TokenResponse buyerRegister(@Valid @RequestBody RegisterBuyerRequest request) {
        Buyer buyer = buyerService.register(request.toCommand());
        return authService.issueTokensForBuyer(buyer);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
