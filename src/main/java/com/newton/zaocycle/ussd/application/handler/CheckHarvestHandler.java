package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.chemical.application.ChemicalService;
import com.newton.zaocycle.farmer.application.FarmerService;
import com.newton.zaocycle.pesticide.application.PesticideApplicationService;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;
import com.newton.zaocycle.shared.domain.PhoneNumber;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Component
public class CheckHarvestHandler implements MenuHandler {

    private static final int MAX_DISPLAY = 3;
    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final FarmerService farmerService;
    private final PesticideApplicationService pesticideService;
    private final ChemicalService chemicalService;

    public CheckHarvestHandler(FarmerService farmerService,
                                PesticideApplicationService pesticideService,
                                ChemicalService chemicalService) {
        this.farmerService = farmerService;
        this.pesticideService = pesticideService;
        this.chemicalService = chemicalService;
    }

    @Override
    public MenuState state() {
        return MenuState.CHECK_HARVEST;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        var farmer = farmerService.findOrCreateByPhone(
                PhoneNumber.of(session.getPhoneNumber()));

        List<PesticideApplication> pending = pesticideService
                .getPendingByFarmer(farmer.id())
                .stream()
                .sorted(Comparator.comparing(PesticideApplication::safeHarvestDate))
                .toList();

        if (pending.isEmpty()) {
            session.setState(MenuState.MAIN_MENU);
            return ResponseBuilder.end(
                    "All your crops are safe to harvest.\n"
                            + "No active pesticide applications recorded.");
        }

        LocalDate today = LocalDate.now(NAIROBI);
        StringBuilder sb = new StringBuilder("Your safe harvest dates:\n");

        List<PesticideApplication> toShow = pending.subList(0, Math.min(MAX_DISPLAY, pending.size()));
        for (PesticideApplication app : toShow) {
            String chemName = resolveChemicalName(app.chemicalId());
            boolean isSafe = !app.safeHarvestDate().isAfter(today);
            String label = isSafe ? "SAFE NOW" : app.safeHarvestDate().format(DATE_FORMAT);
            sb.append(app.crop()).append(" (").append(chemName).append("): ").append(label).append("\n");
        }

        if (pending.size() > MAX_DISPLAY) {
            sb.append("... and ").append(pending.size() - MAX_DISPLAY).append(" more.\n");
        }

        session.setState(MenuState.MAIN_MENU);
        return ResponseBuilder.end(sb.toString().trim());
    }

    private String resolveChemicalName(UUID chemicalId) {
        try {
            return chemicalService.getById(chemicalId).name();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
