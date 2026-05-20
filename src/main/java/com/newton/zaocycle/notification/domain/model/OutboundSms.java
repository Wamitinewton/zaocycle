package com.newton.zaocycle.notification.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class OutboundSms {

    private final UUID id;
    private final String phone;
    private final String templateCode;
    private final String body;
    private final Instant createdAt;
    private String status;
    private String providerMessageId;
    private String errorMessage;
    private Instant sentAt;

    public OutboundSms(UUID id, String phone, String templateCode, String body,
                       String status, String providerMessageId, String errorMessage,
                       Instant sentAt, Instant createdAt) {
        this.id = id;
        this.phone = phone;
        this.templateCode = templateCode;
        this.body = body;
        this.status = status;
        this.providerMessageId = providerMessageId;
        this.errorMessage = errorMessage;
        this.sentAt = sentAt;
        this.createdAt = createdAt;
    }

    public static OutboundSms pending(String phone, String templateCode, String body) {
        Instant now = Instant.now();
        return new OutboundSms(IdGenerator.generate(), phone, templateCode, body,
                "PENDING", null, null, null, now);
    }

    public void markSent(String providerMessageId) {
        this.status = "SENT";
        this.providerMessageId = providerMessageId;
        this.sentAt = Instant.now();
    }

    public void markFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
    }

    public UUID id() {
        return id;
    }

    public String phone() {
        return phone;
    }

    public String templateCode() {
        return templateCode;
    }

    public String body() {
        return body;
    }

    public String status() {
        return status;
    }

    public String providerMessageId() {
        return providerMessageId;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public Instant sentAt() {
        return sentAt;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
