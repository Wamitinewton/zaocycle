package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.collection.application.PickupQueryService;
import com.newton.zaocycle.collection.domain.model.FarmerEarnings;
import com.newton.zaocycle.collection.domain.model.PayoutEntry;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Component
public class CheckEarningsHandler implements MenuHandler {

    private static final DateTimeFormatter SHORT_DATE =
            DateTimeFormatter.ofPattern("d MMM").withLocale(Locale.ENGLISH);

    private final PickupQueryService pickupQueryService;

    public CheckEarningsHandler(PickupQueryService pickupQueryService) {
        this.pickupQueryService = pickupQueryService;
    }

    @Override
    public MenuState state() {
        return MenuState.CHECK_EARNINGS;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        UUID farmerId = UUID.fromString(session.getString("farmerId"));
        FarmerEarnings e = pickupQueryService.getFarmerEarnings(farmerId);

        StringBuilder sb = new StringBuilder("Your earnings:\n");
        sb.append("Total: KSh ").append(e.total().toPlainString()).append("\n");
        sb.append("Month: KSh ").append(e.thisMonth().toPlainString()).append("\n");
        sb.append("Pickups: ").append(e.pickupCount()).append("\n");

        if (!e.recentPayouts().isEmpty()) {
            sb.append("\nRecent:\n");
            for (PayoutEntry p : e.recentPayouts()) {
                sb.append("KSh ").append(p.amount().toPlainString())
                  .append(" - ").append(SHORT_DATE.format(p.date())).append("\n");
            }
        }

        session.setState(MenuState.TERMINATED);
        return ResponseBuilder.end(sb.toString().trim());
    }
}
