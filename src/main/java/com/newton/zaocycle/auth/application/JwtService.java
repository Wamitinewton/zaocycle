package com.newton.zaocycle.auth.application;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.auth.domain.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties props;
    private final StringRedisTemplate redis;
    private final SecretKey secretKey;

    public JwtService(JwtProperties props, StringRedisTemplate redis) {
        this.props = props;
        this.redis = redis;
        this.secretKey = Keys.hmacShaKeyFor(props.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String issueAccessToken(AuthenticatedPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.id().toString())
                .claim("role", principal.role().name())
                .claim("name", principal.displayName())
                .claim("phone", principal.phone())
                .claim("email", principal.email())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.accessTokenMinutes(), ChronoUnit.MINUTES)))
                .signWith(secretKey)
                .compact();
    }

    public String issueRefreshToken(AuthenticatedPrincipal principal) {
        String jti = UUID.randomUUID().toString();
        Instant now = Instant.now();
        String token = Jwts.builder()
                .subject(principal.id().toString())
                .claim("role", principal.role().name())
                .claim("name", principal.displayName())
                .claim("phone", principal.phone())
                .claim("email", principal.email())
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(props.refreshTokenDays(), ChronoUnit.DAYS)))
                .signWith(secretKey)
                .compact();
        redis.opsForValue().set(
                "refresh:" + jti,
                principal.id().toString(),
                Duration.ofDays(props.refreshTokenDays())
        );
        return token;
    }

    public Optional<Claims> parseAccessToken(String token) {
        return parseToken(token);
    }

    public Optional<Claims> parseRefreshToken(String token) {
        return parseToken(token);
    }

    public Optional<AuthenticatedPrincipal> validateRefreshToken(String refreshToken) {
        return parseToken(refreshToken).flatMap(claims -> {
            String jti = claims.getId();
            String stored = redis.opsForValue().get("refresh:" + jti);
            if (stored == null || !stored.equals(claims.getSubject())) {
                return Optional.empty();
            }
            try {
                UUID id = UUID.fromString(claims.getSubject());
                Role role = Role.valueOf(claims.get("role", String.class));
                String displayName = claims.get("name", String.class);
                String phone = claims.get("phone", String.class);
                String email = claims.get("email", String.class);
                return Optional.of(new AuthenticatedPrincipal(id, role, displayName, phone, email));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
    }

    public void revokeRefreshToken(String jti) {
        redis.delete("refresh:" + jti);
    }

    private Optional<Claims> parseToken(String token) {
        try {
            return Optional.of(
                    Jwts.parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload()
            );
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
