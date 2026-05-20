package com.newton.zaocycle.auth.domain.model;

import java.util.UUID;

public record AuthenticatedPrincipal(
        UUID id,
        Role role,
        String displayName,
        String phone,
        String email
) {
}
