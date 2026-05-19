package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.auth.domain.model.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    private static final String SECRET = "test-secret-must-be-at-least-32-characters-long!!";

    private JwtService jwtService;
    private ValueOperations<String, String> redisOps;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(SECRET, 15, 30);
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        redisOps = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(redisOps);
        jwtService = new JwtService(props, redis);
    }

    @Test
    void issueAccessToken_parsesBackWithCorrectClaims() {
        UUID id = UUID.randomUUID();
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                id, Role.FARMER, "John Doe", "+254700000001", null);

        String token = jwtService.issueAccessToken(principal);
        Optional<Claims> parsed = jwtService.parseAccessToken(token);

        assertThat(parsed).isPresent();
        Claims claims = parsed.get();
        assertThat(claims.getSubject()).isEqualTo(id.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("FARMER");
        assertThat(claims.get("name", String.class)).isEqualTo("John Doe");
        assertThat(claims.get("phone", String.class)).isEqualTo("+254700000001");
    }

    @Test
    void parseAccessToken_invalidToken_returnsEmpty() {
        assertThat(jwtService.parseAccessToken("not.a.valid.token")).isEmpty();
    }

    @Test
    void issueRefreshToken_storesInRedis() {
        UUID id = UUID.randomUUID();
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                id, Role.ADMIN, "Admin User", null, "admin@zaocycle.app");

        jwtService.issueRefreshToken(principal);

        verify(redisOps).set(anyString(), eq(id.toString()), any());
    }

    @Test
    void validateRefreshToken_revokedToken_returnsEmpty() {
        UUID id = UUID.randomUUID();
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                id, Role.FARMER, "Jane Farmer", "+254711111111", null);

        when(redisOps.get(anyString())).thenReturn(null);

        String refreshToken = jwtService.issueRefreshToken(principal);
        Optional<AuthenticatedPrincipal> result = jwtService.validateRefreshToken(refreshToken);

        assertThat(result).isEmpty();
    }

    @Test
    void validateRefreshToken_validToken_returnsPrincipal() {
        UUID id = UUID.randomUUID();
        AuthenticatedPrincipal principal = new AuthenticatedPrincipal(
                id, Role.FARMER, "Jane Farmer", "+254711111111", null);

        when(redisOps.get(anyString())).thenReturn(id.toString());

        String refreshToken = jwtService.issueRefreshToken(principal);
        Optional<AuthenticatedPrincipal> result = jwtService.validateRefreshToken(refreshToken);

        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(id);
        assertThat(result.get().role()).isEqualTo(Role.FARMER);
        assertThat(result.get().displayName()).isEqualTo("Jane Farmer");
    }
}
