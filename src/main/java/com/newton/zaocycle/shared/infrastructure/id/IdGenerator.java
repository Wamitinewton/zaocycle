package com.newton.zaocycle.shared.infrastructure.id;

import java.util.UUID;

public final class IdGenerator {

    private IdGenerator() {}

    public static UUID generate() {
        return UUID.randomUUID();
    }
}
