package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

@Component
public class CheckEarningsHandler implements MenuHandler {

    @Override
    public MenuState state() {
        return MenuState.CHECK_EARNINGS;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        session.setState(MenuState.TERMINATED);
        return ResponseBuilder.end(
                "Earnings feature coming soon.\n"
                        + "Your waste collection earnings will appear here.");
    }
}
