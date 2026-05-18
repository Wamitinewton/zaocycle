package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

@Component
public class SchedulePickupHandler implements MenuHandler {

    @Override
    public MenuState state() {
        return MenuState.SCHEDULE_PICKUP;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        session.setState(MenuState.TERMINATED);
        return ResponseBuilder.end(
                "Waste pickup scheduling coming soon.\n"
                        + "We will notify you via SMS when this feature is live.");
    }
}
