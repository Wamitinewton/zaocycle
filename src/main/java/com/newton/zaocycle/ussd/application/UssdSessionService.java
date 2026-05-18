package com.newton.zaocycle.ussd.application;

import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import com.newton.zaocycle.ussd.domain.port.UssdSessionStore;
import org.springframework.stereotype.Service;

@Service
public class UssdSessionService {

    private final UssdSessionStore store;
    private final FarmerService farmerService;

    public UssdSessionService(UssdSessionStore store, FarmerService farmerService) {
        this.store = store;
        this.farmerService = farmerService;
    }

    public UssdSession loadOrCreate(String sessionId, String phoneNumber) {
        return store.find(sessionId).orElseGet(() -> {
            Farmer farmer = farmerService.findOrCreateByPhone(PhoneNumber.of(phoneNumber));
            MenuState initialState = farmer.isRegistrationComplete()
                    ? MenuState.MAIN_MENU : MenuState.WELCOME;
            return UssdSession.start(sessionId, phoneNumber, initialState);
        });
    }

    public void save(UssdSession session) {
        store.save(session);
    }

    public void delete(String sessionId) {
        store.delete(sessionId);
    }
}
