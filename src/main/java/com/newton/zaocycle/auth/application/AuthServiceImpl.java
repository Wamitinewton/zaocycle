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
import com.newton.zaocycle.shared.exception.ValidationException;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public TokenResponse loginUnified(String identifier, String credential) {
        if (looksLikePhone(identifier)) {
            return resolvePhoneLogin(identifier, credential);
        }
        if (looksLikeEmail(identifier)) {
            return resolveEmailLogin(identifier, credential);
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    private boolean looksLikePhone(String identifier) {
        return identifier != null && identifier.matches("^\\+[1-9]\\d{7,14}$");
    }

    private boolean looksLikeEmail(String identifier) {
        return identifier != null && identifier.contains("@");
    }

    private TokenResponse resolvePhoneLogin(String phone, String credential) {
        PhoneNumber phoneNumber;
        try {
            phoneNumber = PhoneNumber.of(phone);
        } catch (ValidationException e) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Optional<Farmer> farmerOpt = farmerService.findByPhone(phoneNumber);
        if (farmerOpt.isPresent()) {
            Farmer f = farmerOpt.get();
            if (!f.isRegistrationComplete() || !passwordEncoder.matches(credential, f.pinHash())) {
                throw new BadCredentialsException("Invalid credentials");
            }
            return buildTokenResponse(new AuthenticatedPrincipal(
                    f.id(), Role.FARMER, f.fullName(), f.phone().value(), null));
        }

        var rider = riderService.findByPhone(phoneNumber)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(credential, rider.passwordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return buildTokenResponse(new AuthenticatedPrincipal(
                rider.id(), Role.RIDER, rider.fullName(), rider.phone().value(), null));
    }

    private TokenResponse resolveEmailLogin(String email, String credential) {
        Optional<StaffUser> staffOpt = staffUserRepository.findByEmail(email);
        if (staffOpt.isPresent()) {
            StaffUser s = staffOpt.get();
            s.ensureActive();
            if (!passwordEncoder.matches(credential, s.passwordHash())) {
                throw new BadCredentialsException("Invalid credentials");
            }
            return buildTokenResponse(new AuthenticatedPrincipal(
                    s.id(), s.role(), s.fullName(), null, s.email()));
        }

        var buyer = buyerService.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!buyer.isActive()) {
            throw new ValidationException("Account is inactive");
        }
        if (!passwordEncoder.matches(credential, buyer.passwordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return buildTokenResponse(new AuthenticatedPrincipal(
                buyer.id(), Role.BUYER, buyer.displayName(), buyer.phone(), buyer.email()));
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
