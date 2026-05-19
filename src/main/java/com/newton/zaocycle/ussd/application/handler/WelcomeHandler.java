package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

@Component
public class WelcomeHandler implements MenuHandler {

    private final FarmerService farmerService;

    public WelcomeHandler(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @Override
    public MenuState state() {
        return MenuState.WELCOME;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        Farmer farmer = farmerService.findOrCreateByPhone(PhoneNumber.of(session.getPhoneNumber()));

        if (farmer.isRegistrationComplete()) {
            session.put("farmerId", farmer.id().toString());
            session.setState(MenuState.MAIN_MENU);
            return ResponseBuilder.cont(
                    "Karibu, " + firstName(farmer.fullName()) + ".\n"
                    + "1. Log pesticide spray\n"
                    + "2. Check safe harvest date\n"
                    + "3. Schedule waste pickup\n"
                    + "4. My earnings\n"
                    + "0. Exit");
        }

        session.setState(MenuState.REGISTER_NAME);
        return ResponseBuilder.cont(
                "Welcome to ZaoCycle.\n"
                + "You are not yet registered.\n"
                + "Enter your full name:");
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.split("\\s+")[0];
    }
}
