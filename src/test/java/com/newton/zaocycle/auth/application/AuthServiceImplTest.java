package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.api.dto.TokenResponse;
import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.auth.domain.model.Role;
import com.newton.zaocycle.auth.domain.model.StaffUser;
import com.newton.zaocycle.auth.domain.port.StaffUserRepository;
import com.newton.zaocycle.buyer.application.BuyerService;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.rider.application.RiderService;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private FarmerService farmerService;
    @Mock private RiderService riderService;
    @Mock private BuyerService buyerService;
    @Mock private StaffUserRepository staffUserRepository;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    private AuthServiceImpl authService;

    private static final JwtProperties JWT_PROPS =
            new JwtProperties("test-secret-must-be-at-least-32-chars!!", 15, 30);

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                farmerService, riderService, buyerService,
                staffUserRepository, jwtService, JWT_PROPS, passwordEncoder);
    }

    @Test
    void loginUnified_farmerPhone_validPin_returnsToken() {
        UUID id = UUID.randomUUID();
        PhoneNumber phone = PhoneNumber.of("+254700000001");
        Farmer farmer = new Farmer(id, phone, "John Doe", Ward.MWEA, "hashed-pin", true, null, null, null, null, Instant.now(), Instant.now());

        when(farmerService.findByPhone(phone)).thenReturn(Optional.of(farmer));
        when(passwordEncoder.matches("1234", "hashed-pin")).thenReturn(true);
        when(jwtService.issueAccessToken(any())).thenReturn("access-token");
        when(jwtService.issueRefreshToken(any())).thenReturn("refresh-token");

        TokenResponse response = authService.loginUnified("+254700000001", "1234");

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user().role()).isEqualTo("FARMER");
    }

    @Test
    void loginUnified_unknownPhone_throwsBadCredentials() {
        when(farmerService.findByPhone(any())).thenReturn(Optional.empty());
        when(riderService.findByPhone(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.loginUnified("+254700000002", "1234"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginUnified_incompleteRegistration_throwsBadCredentials() {
        UUID id = UUID.randomUUID();
        PhoneNumber phone = PhoneNumber.of("+254700000003");
        Farmer farmer = new Farmer(id, phone, null, null, null, false, null, null, null, null, Instant.now(), Instant.now());

        when(farmerService.findByPhone(phone)).thenReturn(Optional.of(farmer));

        assertThatThrownBy(() -> authService.loginUnified("+254700000003", "1234"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginUnified_wrongPin_throwsBadCredentials() {
        UUID id = UUID.randomUUID();
        PhoneNumber phone = PhoneNumber.of("+254700000004");
        Farmer farmer = new Farmer(id, phone, "Jane Doe", Ward.NDIA, "hashed-pin", true, null, null, null, null, Instant.now(), Instant.now());

        when(farmerService.findByPhone(phone)).thenReturn(Optional.of(farmer));
        when(passwordEncoder.matches("wrong", "hashed-pin")).thenReturn(false);

        assertThatThrownBy(() -> authService.loginUnified("+254700000004", "wrong"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void loginUnified_inactiveStaff_throwsValidation() {
        StaffUser staff = new StaffUser(UUID.randomUUID(), "admin@test.com", "hash",
                "Admin", Role.ADMIN, false, Instant.now(), Instant.now());

        when(staffUserRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(staff));

        assertThatThrownBy(() -> authService.loginUnified("admin@test.com", "password"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void loginUnified_invalidIdentifierFormat_throwsBadCredentials() {
        assertThatThrownBy(() -> authService.loginUnified("not-a-phone-or-email", "secret"))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void refresh_invalidToken_throwsValidation() {
        when(jwtService.validateRefreshToken(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("invalid-token"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void refresh_validToken_issuesNewTokens() {
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                UUID.randomUUID(), Role.FARMER, "John", "+254700000001", null);

        when(jwtService.validateRefreshToken("valid-refresh")).thenReturn(Optional.of(principal));
        when(jwtService.parseRefreshToken("valid-refresh")).thenReturn(Optional.empty());
        when(jwtService.issueAccessToken(any())).thenReturn("new-access");
        when(jwtService.issueRefreshToken(any())).thenReturn("new-refresh");

        TokenResponse response = authService.refresh("valid-refresh");

        assertThat(response.accessToken()).isEqualTo("new-access");
        assertThat(response.refreshToken()).isEqualTo("new-refresh");
    }
}
