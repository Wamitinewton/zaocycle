package com.newton.zaocycle.ussd.domain.model;

import java.time.Instant;
import java.util.UUID;

public final class SessionLog {

    private final UUID id;
    private final String sessionId;
    private final String phone;
    private final String serviceCode;
    private final String inputText;
    private final String responseText;
    private final String responseType;
    private final Integer durationMs;
    private final String errorMessage;
    private final Instant createdAt;

    public SessionLog(UUID id, String sessionId, String phone, String serviceCode,
                      String inputText, String responseText, String responseType,
                      Integer durationMs, String errorMessage, Instant createdAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.phone = phone;
        this.serviceCode = serviceCode;
        this.inputText = inputText;
        this.responseText = responseText;
        this.responseType = responseType;
        this.durationMs = durationMs;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
    }

    public UUID id()             { return id; }
    public String sessionId()    { return sessionId; }
    public String phone()        { return phone; }
    public String serviceCode()  { return serviceCode; }
    public String inputText()    { return inputText; }
    public String responseText() { return responseText; }
    public String responseType() { return responseType; }
    public Integer durationMs()  { return durationMs; }
    public String errorMessage() { return errorMessage; }
    public Instant createdAt()   { return createdAt; }
}
