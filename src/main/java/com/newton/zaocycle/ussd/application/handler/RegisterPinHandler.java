package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.farmer.application.command.RegisterFarmerCommand;
import com.newton.zaocycle.farmer.domain.model.Farmer;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.shared.domain.Ward;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RegisterPinHandler implements MenuHandler {

    private static final Logger log = LoggerFactory.getLogger(RegisterPinHandler.class);

    private final FarmerService farmerService;

    public RegisterPinHandler(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @Override
    public MenuState state() {
        return MenuState.REGISTER_PIN;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        if (!isValidPin(input)) {
            return ResponseBuilder.cont("PIN must be exactly 4 digits.\nSet a 4-digit PIN:");
        }

        String fullName    = session.getString("fullName");
        String wardName    = session.getString("ward");

        if (fullName == null || wardName == null) {
            log.warn("Registration session missing data for phone={}", session.getPhoneNumber());
            return ResponseBuilder.end("Session expired. Please dial again.");
        }

        Ward ward;
        try {
            ward = Ward.valueOf(wardName);
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.end("Session error. Please dial again.");
        }

        String tradingCenter = session.getString("tradingCenter");
        Double latitude      = parseDouble(session.getString("latitude"));
        Double longitude     = parseDouble(session.getString("longitude"));

        // PIN is passed directly to FarmerService; it is hashed there and never stored in the session
        RegisterFarmerCommand command = new RegisterFarmerCommand(
                PhoneNumber.of(session.getPhoneNumber()),
                fullName,
                ward,
                input.trim(),
                tradingCenter,
                latitude,
                longitude
        );
        try {
            Farmer farmer = farmerService.register(command);
            session.put("farmerId", farmer.id().toString());
            session.setState(MenuState.TERMINATED);
            return ResponseBuilder.end(
                    "Welcome to ZaoCycle, " + firstName(farmer.fullName()) + ".\n"
                    + "Dial this shortcode again to log sprays.");
        } catch (Exception e) {
            log.error("Registration failed for phone={}", session.getPhoneNumber(), e);
            return ResponseBuilder.end("Registration failed. Please try again.");
        }
    }

    private boolean isValidPin(String input) {
        if (input == null) return false;
        return input.trim().matches("\\d{4}");
    }

    private String firstName(String fullName) {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.split("\\s+")[0];
    }

    private Double parseDouble(String s) {
        if (s == null) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
