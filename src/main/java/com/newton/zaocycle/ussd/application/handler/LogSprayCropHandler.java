package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogSprayCropHandler implements MenuHandler {

    private static final List<String> CROPS =
            List.of("Tomatoes", "French Beans", "Cabbage", "Onions", "Potatoes");

    private final LogSprayChemicalHandler chemicalHandler;

    public LogSprayCropHandler(LogSprayChemicalHandler chemicalHandler) {
        this.chemicalHandler = chemicalHandler;
    }

    public static String cropMenuText() {
        StringBuilder sb = new StringBuilder("Select crop sprayed:\n");
        for (int i = 0; i < CROPS.size(); i++) {
            sb.append(i + 1).append(". ").append(CROPS.get(i)).append("\n");
        }
        sb.append("0. Back");
        return sb.toString();
    }

    @Override
    public MenuState state() {
        return MenuState.LOG_SPRAY_CROP;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        if ("0".equals(input)) {
            session.setState(MenuState.MAIN_MENU);
            return ResponseBuilder.cont(mainMenuText());
        }

        if (input == null || input.isBlank()) {
            return ResponseBuilder.cont(cropMenuText());
        }

        int choice = parseChoice(input);
        if (choice < 1 || choice > CROPS.size()) {
            return ResponseBuilder.cont(cropMenuText());
        }

        session.put("crop", CROPS.get(choice - 1));
        session.put("chemicalPage", "0");
        session.setState(MenuState.LOG_SPRAY_CHEMICAL);
        return chemicalHandler.handle(session, "");
    }

    private int parseChoice(String input) {
        try {
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String mainMenuText() {
        return """
                1. Log pesticide spray
                2. Check safe harvest date
                3. Schedule waste pickup
                4. My earnings
                0. Exit""";
    }
}
