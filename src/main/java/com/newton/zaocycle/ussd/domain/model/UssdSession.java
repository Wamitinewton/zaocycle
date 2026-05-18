package com.newton.zaocycle.ussd.domain.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class UssdSession {

    private String sessionId;
    private String phoneNumber;
    private MenuState state;
    private Map<String, Object> data;
    private Instant startedAt;
    private Instant updatedAt;

    public UssdSession() {
        this.data = new HashMap<>();
    }

    public static UssdSession start(String sessionId, String phoneNumber, MenuState initialState) {
        UssdSession s = new UssdSession();
        s.sessionId = sessionId;
        s.phoneNumber = phoneNumber;
        s.state = initialState;
        s.startedAt = Instant.now();
        s.updatedAt = s.startedAt;
        return s;
    }

    public void put(String key, Object value) {
        data.put(key, value);
        updatedAt = Instant.now();
    }

    public String getString(String key) {
        Object v = data.get(key);
        return v != null ? v.toString() : null;
    }

    public int getInt(String key, int defaultValue) {
        Object v = data.get(key);
        if (v == null) return defaultValue;
        if (v instanceof Number n) return n.intValue();
        try {
            return Integer.parseInt(v.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Used by application code and Jackson deserialization; updatedAt side-effect is harmless during deserialization
    public void setState(MenuState state) {
        this.state = state;
        this.updatedAt = Instant.now();
    }

    public String getSessionId()        { return sessionId; }
    public String getPhoneNumber()      { return phoneNumber; }
    public MenuState getState()         { return state; }
    public Map<String, Object> getData() { return data; }
    public Instant getStartedAt()       { return startedAt; }
    public Instant getUpdatedAt()       { return updatedAt; }

    public void setSessionId(String sessionId)      { this.sessionId = sessionId; }
    public void setPhoneNumber(String phoneNumber)  { this.phoneNumber = phoneNumber; }
    public void setData(Map<String, Object> data)   { this.data = data; }
    public void setStartedAt(Instant startedAt)     { this.startedAt = startedAt; }
    public void setUpdatedAt(Instant updatedAt)     { this.updatedAt = updatedAt; }
}
