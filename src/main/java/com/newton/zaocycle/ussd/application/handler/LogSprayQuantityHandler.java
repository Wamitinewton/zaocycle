package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class LogSprayQuantityHandler implements MenuHandler {

    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @Override
    public MenuState state() {
        return MenuState.LOG_SPRAY_QUANTITY;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        if ("0".equals(input)) {
            session.setState(MenuState.LOG_SPRAY_CHEMICAL);
            session.put("chemicalPage", "0");
            return ResponseBuilder.cont("Select chemical applied:\n0. Back");
        }

        if (input == null || input.isBlank()) {
            return ResponseBuilder.cont("Enter quantity sprayed in millilitres (e.g. 500):");
        }

        double qty;
        try {
            qty = Double.parseDouble(input.trim());
            if (qty <= 0 || qty > 100_000) throw new NumberFormatException("out of range");
        } catch (NumberFormatException e) {
            return ResponseBuilder.cont("Please enter a valid number (e.g. 500):");
        }

        session.put("quantityMl", String.valueOf(qty));
        session.setState(MenuState.LOG_SPRAY_CONFIRM);
        return ResponseBuilder.cont(buildConfirmMenu(session, qty));
    }

    private String buildConfirmMenu(UssdSession session, double qty) {
        String crop = session.getString("crop");
        String chemName = session.getString("chemicalName");
        int halfLife = session.getInt("halfLifeDays", 0);
        int phi = session.getInt("phiDays", 0);
        int waitDays = Math.max(halfLife, phi);

        String safeDate = LocalDate.now(NAIROBI).plusDays(waitDays).format(DATE_FORMAT);

        return "Confirm spray log:\n"
                + "Crop: " + crop + "\n"
                + "Chemical: " + chemName + "\n"
                + "Quantity: " + (int) qty + " ml\n"
                + "Safe harvest from: " + safeDate + "\n\n"
                + "1. Confirm\n"
                + "2. Cancel";
    }
}
