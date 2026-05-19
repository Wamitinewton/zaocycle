package com.newton.zaocycle.auth.api;

import com.newton.zaocycle.auth.api.dto.*;
import com.newton.zaocycle.auth.application.AuthService;
import com.newton.zaocycle.buyer.api.dto.RegisterBuyerRequest;
import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/buyer/register")
    public TokenResponse buyerRegister(@Valid @RequestBody RegisterBuyerRequest request) {
        Buyer buyer = buyerService.register(request.toCommand());
        return authService.issueTokensForBuyer(buyer);
    }

    @PostMapping("/farmer/login")
    public TokenResponse farmerLogin(@Valid @RequestBody FarmerLoginRequest request) {
        return authService.loginFarmer(request.phone(), request.pin());
    }

    @PostMapping("/rider/login")
    public TokenResponse riderLogin(@Valid @RequestBody RiderLoginRequest request) {
        return authService.loginRider(request.phone(), request.password());
    }

    @PostMapping("/staff/login")
    public TokenResponse staffLogin(@Valid @RequestBody StaffLoginRequest request) {
        return authService.loginStaff(request.email(), request.password());
    }

    @PostMapping("/buyer/login")
    public TokenResponse buyerLogin(@Valid @RequestBody BuyerLoginRequest request) {
        return authService.loginBuyer(request.email(), request.password());
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
