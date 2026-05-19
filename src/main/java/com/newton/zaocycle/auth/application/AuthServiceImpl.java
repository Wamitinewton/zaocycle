package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.api.dto.TokenResponse;
import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.auth.domain.model.Role;
import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.buyer.domain.model.Buyer;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.exception.NotFoundException;
import com.newton.zaocycle.shared.exception.ValidationException;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AuthServiceImpl implements AuthService {

    private final FarmerService farmerService;
    private final RiderService riderService;
    private final BuyerService buyerService;
    private final StaffUserRepository staffUserRepository;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;

    AuthServiceImpl(FarmerService farmerService,
                    RiderService riderService,
                    BuyerService buyerService,
                    StaffUserRepository staffUserRepository,
                    JwtService jwtService,
                    JwtProperties jwtProperties,
                    PasswordEncoder passwordEncoder) {
        this.farmerService = farmerService;
        this.riderService = riderService;
        this.buyerService = buyerService;
        this.staffUserRepository = staffUserRepository;
        this.jwtService = jwtService;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse loginFarmer(String phone, String pin) {
        Farmer farmer = farmerService.findByPhone(PhoneNumber.of(phone))
                .orElseThrow(() -> new NotFoundException("Farmer not found"));
        if (!farmer.isRegistrationComplete()) {
            throw new ValidationException("Farmer registration is not complete");
        }
        if (!passwordEncoder.matches(pin, farmer.pinHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                farmer.id(), Role.FARMER, farmer.fullName(), farmer.phone().value(), null);
        return buildTokenResponse(principal);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse loginRider(String phone, String password) {
        var rider = riderService.findByPhone(PhoneNumber.of(phone))
                .orElseThrow(() -> new NotFoundException("Rider not found"));
        if (!passwordEncoder.matches(password, rider.passwordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                rider.id(), Role.RIDER, rider.fullName(), rider.phone().value(), null);
        return buildTokenResponse(principal);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse loginStaff(String email, String password) {
        StaffUser staff = staffUserRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Staff user not found"));
        staff.ensureActive();
        if (!passwordEncoder.matches(password, staff.passwordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                staff.id(), staff.role(), staff.fullName(), null, staff.email());
        return buildTokenResponse(principal);
    }

    @Override
    @Transactional(readOnly = true)
    public TokenResponse loginBuyer(String email, String password) {
        var buyer = buyerService.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Buyer not found"));
        if (!buyer.isActive()) {
            throw new ValidationException("Buyer account is inactive");
        }
        if (!passwordEncoder.matches(password, buyer.passwordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                buyer.id(), Role.BUYER, buyer.displayName(), buyer.phone(), buyer.email());
        return buildTokenResponse(principal);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        AuthenticatedPrincipal principal = jwtService.validateRefreshToken(refreshToken)
                .orElseThrow(() -> new ValidationException("Invalid or expired refresh token"));
        jwtService.parseRefreshToken(refreshToken)
                .map(Claims::getId)
                .ifPresent(jwtService::revokeRefreshToken);
        return buildTokenResponse(principal);
    }

    @Override
    public void logout(String refreshToken) {
        jwtService.parseRefreshToken(refreshToken)
                .map(Claims::getId)
                .ifPresent(jwtService::revokeRefreshToken);
    }

    @Override
    public TokenResponse issueTokensForBuyer(Buyer buyer) {
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                buyer.id(), Role.BUYER, buyer.displayName(), buyer.phone(), buyer.email());
        return buildTokenResponse(principal);
    }

    @Override
    public TokenResponse issueTokensForFarmer(Farmer farmer) {
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                farmer.id(), Role.FARMER, farmer.fullName(), farmer.phone().value(), null);
        return buildTokenResponse(principal);
    }

    private TokenResponse buildTokenResponse(AuthenticatedPrincipal principal) {
        String accessToken = jwtService.issueAccessToken(principal);
        String refreshToken = jwtService.issueRefreshToken(principal);
        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtProperties.accessTokenMinutes() * 60,
                new TokenResponse.UserSummary(
                        principal.id().toString(),
                        principal.role().name(),
                        principal.displayName()
                )
        );
    }
}
