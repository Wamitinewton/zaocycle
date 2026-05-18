package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

@Component
public class RegisterNameHandler implements MenuHandler {

    @Override
    public MenuState state() {
        return MenuState.REGISTER_NAME;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        if (input == null || input.isBlank()) {
            return ResponseBuilder.cont("Please enter your full name:");
        }

        String name = input.trim();
        if (name.length() < 2 || name.length() > 255) {
            return ResponseBuilder.cont("Name must be between 2 and 255 characters.\nEnter your full name:");
        }

        session.put("fullName", name);
        session.setState(MenuState.REGISTER_WARD);
        return ResponseBuilder.cont(wardMenu());
    }

    private String wardMenu() {
        return """
                Select your ward:
                1. Mwea
                2. Gichugu
                3. Kirinyaga Central
                4. Ndia""";
    }
}
