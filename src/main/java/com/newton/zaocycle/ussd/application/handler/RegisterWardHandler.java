package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.ValidationException;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

@Component
public class RegisterWardHandler implements MenuHandler {

    @Override
    public MenuState state() {
        return MenuState.REGISTER_WARD;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        int choice = parseChoice(input);
        Ward ward;
        try {
            ward = Ward.fromIndex(choice);
        } catch (ValidationException e) {
            return ResponseBuilder.cont("Invalid selection.\n" + wardMenu());
        }

        session.put("ward", ward.name());
        session.setState(MenuState.REGISTER_PIN);
        return ResponseBuilder.cont("Set a 4-digit PIN\n(used to access your account online):");
    }

    private int parseChoice(String input) {
        if (input == null || input.isBlank()) return -1;
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String wardMenu() {
        return "Select your ward:\n"
                + "1. Mwea\n"
                + "2. Gichugu\n"
                + "3. Kirinyaga Central\n"
                + "4. Ndia";
    }
}
