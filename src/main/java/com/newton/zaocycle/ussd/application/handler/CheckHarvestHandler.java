package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class CheckHarvestHandler implements MenuHandler {

    private static final DateTimeFormatter SHORT_DATE =
            DateTimeFormatter.ofPattern("d MMM").withLocale(Locale.ENGLISH);

    private final PesticideApplicationService pesticideService;

    public CheckHarvestHandler(PesticideApplicationService pesticideService) {
        this.pesticideService = pesticideService;
    }

    @Override
    public MenuState state() {
        return MenuState.CHECK_HARVEST;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        UUID farmerId = UUID.fromString(session.getString("farmerId"));
        List<PesticideApplication> recent = pesticideService.findRecentByFarmer(farmerId, 14);

        if (recent.isEmpty()) {
            session.setState(MenuState.TERMINATED);
            return ResponseBuilder.end("No recent sprays logged.\nUse menu 1 to log a spray.");
        }

        StringBuilder sb = new StringBuilder("Your harvest schedule:\n");
        int limit = Math.min(recent.size(), 4);
        for (int i = 0; i < limit; i++) {
            PesticideApplication app = recent.get(i);
            sb.append(i + 1).append(". ")
              .append(truncate(app.crop(), 10)).append(" ")
              .append(SHORT_DATE.format(app.safeHarvestDate())).append(" ")
              .append(statusLabel(app.status())).append("\n");
        }
        if (recent.size() > limit) {
            sb.append("(").append(recent.size() - limit).append(" more)\n");
        }

        session.setState(MenuState.TERMINATED);
        return ResponseBuilder.end(sb.toString().trim());
    }

    private static String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max);
    }

    private static String statusLabel(ApplicationStatus status) {
        return switch (status) {
            case SAFE -> "READY";
            case PENDING -> "wait";
            default -> "done";
        };
    }
}
