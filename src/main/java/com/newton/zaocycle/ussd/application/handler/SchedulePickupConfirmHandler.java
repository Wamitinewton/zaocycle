package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.collection.application.event.WastePickupRequested;
import com.newton.zaocycle.collection.domain.model.PickupSource;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.ussd.application.MenuDispatcher;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SchedulePickupConfirmHandler implements MenuHandler {

    private final PesticideApplicationService pesticideService;
    private final ApplicationEventPublisher events;
    private final MenuDispatcher menuDispatcher;

    public SchedulePickupConfirmHandler(PesticideApplicationService pesticideService,
                                        ApplicationEventPublisher events,
                                        @Lazy MenuDispatcher menuDispatcher) {
        this.pesticideService = pesticideService;
        this.events = events;
        this.menuDispatcher = menuDispatcher;
    }

    @Override
    public MenuState state() {
        return MenuState.SCHEDULE_PICKUP_CONFIRM;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        UUID applicationId = UUID.fromString(session.getString("selectedApplicationId"));
        UUID farmerId = UUID.fromString(session.getString("farmerId"));
        PesticideApplication app = pesticideService.findById(applicationId);

        if (input == null || input.isBlank()) {
            return ResponseBuilder.cont(
                    "Confirm pickup for:\n" + app.crop() + "\n"
                    + "Pickup tomorrow morning.\n\n"
                    + "1. Confirm\n2. Cancel");
        }

        if ("1".equals(input)) {
            events.publishEvent(new WastePickupRequested(farmerId, applicationId, PickupSource.USSD));
            session.setState(MenuState.TERMINATED);
            return ResponseBuilder.end("Pickup scheduled.\nYou will receive an SMS confirmation.");
        }

        session.put("pickupOptions", null);
        session.setState(MenuState.MAIN_MENU);
        return menuDispatcher.dispatch(session, "");
    }
}
