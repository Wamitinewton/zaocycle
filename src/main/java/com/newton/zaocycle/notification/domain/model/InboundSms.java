package com.newton.zaocycle.notification.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.time.Instant;
import java.util.UUID;

public final class InboundSms {

    private final UUID id;
    private final String phone;
    private final String body;
    private String commandParsed;
    private String status;
    private String errorMessage;
    private final Instant receivedAt;

    public InboundSms(UUID id, String phone, String body, String commandParsed,
                      String status, String errorMessage, Instant receivedAt) {
        this.id = id;
        this.phone = phone;
        this.body = body;
        this.commandParsed = commandParsed;
        this.status = status;
        this.errorMessage = errorMessage;
        this.receivedAt = receivedAt;
    }

    public static InboundSms received(String phone, String body) {
        return new InboundSms(IdGenerator.generate(), phone, body, null, "RECEIVED", null, Instant.now());
    }

    public void setCommandParsed(String command) {
        this.commandParsed = command;
    }

    public void markProcessed() {
        this.status = "PROCESSED";
    }

    public void markRejected(String reason) {
        this.status = "REJECTED";
        this.errorMessage = reason;
    }

    public UUID id()                { return id; }
    public String phone()           { return phone; }
    public String body()            { return body; }
    public String commandParsed()   { return commandParsed; }
    public String status()          { return status; }
    public String errorMessage()    { return errorMessage; }
    public Instant receivedAt()     { return receivedAt; }
}
