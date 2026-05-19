package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import com.newton.zaocycle.ussd.domain.port.UssdSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UssdSessionService {

    private static final Logger log = LoggerFactory.getLogger(UssdSessionService.class);

    private final UssdSessionStore store;

    public UssdSessionService(UssdSessionStore store) {
        this.store = store;
    }

    public UssdSession loadOrCreate(String sessionId, String phoneNumber) {
        return store.find(sessionId).map(s -> {
            log.debug("Resumed session sessionId={} phone={} state={}", sessionId, phoneNumber, s.getState());
            return s;
        }).orElseGet(() -> {
            log.info("New USSD session sessionId={} phone={}", sessionId, phoneNumber);
            return UssdSession.start(sessionId, phoneNumber, MenuState.WELCOME);
        });
    }

    public void save(UssdSession session) {
        store.save(session);
    }

    public void delete(String sessionId) {
        store.delete(sessionId);
    }
}
