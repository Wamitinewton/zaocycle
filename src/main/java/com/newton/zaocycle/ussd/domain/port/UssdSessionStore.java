package com.newton.zaocycle.ussd.domain.port;

import com.newton.zaocycle.ussd.domain.model.UssdSession;

import java.util.Optional;

public interface UssdSessionStore {
    Optional<UssdSession> find(String sessionId);
    void save(UssdSession session);
    void delete(String sessionId);
}
