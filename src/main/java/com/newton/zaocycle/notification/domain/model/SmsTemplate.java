package com.newton.zaocycle.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

public final class SmsTemplate {

    private final UUID id;
    private final String code;
    private final String body;
    private final String language;
    private final Instant createdAt;

    public SmsTemplate(UUID id, String code, String body, String language, Instant createdAt) {
        this.id = id;
        this.code = code;
        this.body = body;
        this.language = language;
        this.createdAt = createdAt;
    }

    public UUID id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String body() {
        return body;
    }

    public String language() {
        return language;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
