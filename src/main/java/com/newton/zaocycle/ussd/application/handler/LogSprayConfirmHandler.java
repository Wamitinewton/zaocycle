package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.application.command.LogPesticideApplicationCommand;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class LogSprayConfirmHandler implements MenuHandler {

    private static final Logger log = LoggerFactory.getLogger(LogSprayConfirmHandler.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final PesticideApplicationService pesticideService;
    private final FarmerService farmerService;

    public LogSprayConfirmHandler(PesticideApplicationService pesticideService,
                                  FarmerService farmerService) {
        this.pesticideService = pesticideService;
        this.farmerService = farmerService;
    }

    @Override
    public MenuState state() {
        return MenuState.LOG_SPRAY_CONFIRM;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        if ("2".equals(input) || "0".equals(input)) {
            session.setState(MenuState.MAIN_MENU);
            return ResponseBuilder.cont("""
                    Spray log cancelled.
                    
                    1. Log pesticide spray
                    2. Check safe harvest date
                    3. Schedule waste pickup
                    4. My earnings
                    0. Exit""");
        }

        if (!"1".equals(input)) {
            return ResponseBuilder.cont("Enter 1 to confirm or 2 to cancel:");
        }

        String chemicalIdStr = session.getString("chemicalId");
        String crop = session.getString("crop");
        String qtyStr = session.getString("quantityMl");

        if (chemicalIdStr == null || crop == null) {
            return ResponseBuilder.end("Session data lost. Please dial again.");
        }

        try {
            var farmer = farmerService.findOrCreateByPhone(
                    PhoneNumber.of(session.getPhoneNumber()));
            double qty = qtyStr != null ? Double.parseDouble(qtyStr) : 0.0;

            LogPesticideApplicationCommand command = new LogPesticideApplicationCommand(
                    farmer.id(),
                    UUID.fromString(chemicalIdStr),
                    crop,
                    qty > 0 ? qty : null
            );

            PesticideApplication application = pesticideService.log(command);
            session.setState(MenuState.TERMINATED);

            return ResponseBuilder.end(
                    "Spray logged.\n"
                            + "Safe to harvest from "
                            + application.safeHarvestDate().format(DATE_FORMAT) + ".\n"
                            + "You will receive an SMS reminder.");

        } catch (Exception e) {
            log.error("Failed to log spray for phone={}", session.getPhoneNumber(), e);
            return ResponseBuilder.end("Failed to log spray. Please try again.");
        }
    }
}
