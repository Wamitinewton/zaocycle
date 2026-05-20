package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.ussd.application.MenuDispatcher;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class SchedulePickupSelectHandler implements MenuHandler {

    private final PesticideApplicationService pesticideService;
    private final PickupQueryService pickupQueryService;
    private final MenuDispatcher menuDispatcher;

    public SchedulePickupSelectHandler(PesticideApplicationService pesticideService,
                                       PickupQueryService pickupQueryService,
                                       @Lazy MenuDispatcher menuDispatcher) {
        this.pesticideService = pesticideService;
        this.pickupQueryService = pickupQueryService;
        this.menuDispatcher = menuDispatcher;
    }

    private static int parseChoice(String input, int max) {
        try {
            int n = Integer.parseInt(input.trim());
            return (n >= 1 && n <= max) ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public MenuState state() {
        return MenuState.SCHEDULE_PICKUP_SELECT;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        UUID farmerId = UUID.fromString(session.getString("farmerId"));

        if (!session.has("pickupOptions")) {
            boolean hasOpen = pickupQueryService.hasOpenPickup(farmerId);
            if (hasOpen) {
                session.setState(MenuState.TERMINATED);
                return ResponseBuilder.end("You already have a pickup scheduled.\nWait for the rider.");
            }

            List<PesticideApplication> eligible = pesticideService.findReadyForPickup(farmerId);
            if (eligible.isEmpty()) {
                session.setState(MenuState.TERMINATED);
                return ResponseBuilder.end("No harvests ready for pickup.\nLog a spray first (menu 1).");
            }

            List<String> ids = eligible.stream().map(a -> a.id().toString()).toList();
            session.put("pickupOptions", ids);

            StringBuilder sb = new StringBuilder("Select pickup:\n");
            int limit = Math.min(eligible.size(), 4);
            for (int i = 0; i < limit; i++) {
                sb.append(i + 1).append(". ").append(eligible.get(i).crop()).append("\n");
            }
            sb.append("0. Cancel");
            return ResponseBuilder.cont(sb.toString());
        }

        @SuppressWarnings("unchecked")
        List<String> ids = (List<String>) session.get("pickupOptions");

        if ("0".equals(input)) {
            session.put("pickupOptions", null);
            session.setState(MenuState.MAIN_MENU);
            return menuDispatcher.dispatch(session, "");
        }

        int choice = parseChoice(input, ids.size());
        if (choice < 1) {
            return ResponseBuilder.cont("Invalid choice.\n0. Cancel");
        }

        session.put("selectedApplicationId", ids.get(choice - 1));
        session.setState(MenuState.SCHEDULE_PICKUP_CONFIRM);
        return menuDispatcher.dispatch(session, "");
    }
}
