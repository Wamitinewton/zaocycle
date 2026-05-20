package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.shared.domain.TradingCenter;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.shared.exception.ValidationException;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
public class RegisterVillageHandler implements MenuHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterVillageHandler.class);

    @Override
    public MenuState state() {
        return MenuState.REGISTER_VILLAGE;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        String wardName = session.getString("ward");
        if (wardName == null) {
            log.warn("Ward missing from session sessionId={}", session.getSessionId());
            return ResponseBuilder.end("Session expired. Please dial again.");
        }

        Ward ward;
        try {
            ward = Ward.valueOf(wardName);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.end("Session error. Please dial again.");
        }

        int choice = parseChoice(input);
        TradingCenter center;
        try {
            center = TradingCenter.fromIndex(ward, choice);
        } catch (ValidationException e) {
            return ResponseBuilder.cont("Invalid selection.\n" + villageMenu(ward));
        }

        session.put("tradingCenter", center.displayName());
        session.put("latitude", String.valueOf(center.latitude()));
        session.put("longitude", String.valueOf(center.longitude()));
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

    private String villageMenu(Ward ward) {
        List<TradingCenter> centers = TradingCenter.forWard(ward);
        StringBuilder sb = new StringBuilder("Select your nearest trading centre:\n");
        IntStream.range(0, centers.size())
                .forEach(i -> sb.append(i + 1).append(". ").append(centers.get(i).displayName()).append("\n"));
        return sb.toString().stripTrailing();
    }
}
