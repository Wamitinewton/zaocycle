package com.newton.zaocycle.buyer.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.buyer.api.dto.BuyerProfileResponse;
import com.newton.zaocycle.buyer.api.dto.UpdateBuyerRequest;
import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.shared.exception.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/buyer")
@PreAuthorize("hasRole('BUYER')")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @GetMapping("/me")
    public BuyerProfileResponse getProfile(@AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return buyerService.findById(principal.id())
                .map(BuyerProfileResponse::from)
                .orElseThrow(() -> new NotFoundException("Buyer not found"));
    }

    @PatchMapping("/me")
    public BuyerProfileResponse updateProfile(@AuthenticationPrincipal AuthenticatedPrincipal principal,
                                              @Valid @RequestBody UpdateBuyerRequest request) {
        return BuyerProfileResponse.from(buyerService.updateProfile(principal.id(), request.toCommand()));
    }
}
