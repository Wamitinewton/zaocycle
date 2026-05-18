package com.newton.zaocycle.ussd.application.handler;

import com.newton.zaocycle.chemical.application.ChemicalService;
import com.newton.zaocycle.chemical.application.dto.ChemicalSummary;
import com.newton.zaocycle.ussd.application.response.MenuResponse;
import com.newton.zaocycle.ussd.application.response.ResponseBuilder;
import com.newton.zaocycle.ussd.domain.model.MenuState;
import com.newton.zaocycle.ussd.domain.model.UssdSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LogSprayChemicalHandler implements MenuHandler {

    private static final int PAGE_SIZE = 4;

    private final ChemicalService chemicalService;

    public LogSprayChemicalHandler(ChemicalService chemicalService) {
        this.chemicalService = chemicalService;
    }

    @Override
    public MenuState state() {
        return MenuState.LOG_SPRAY_CHEMICAL;
    }

    @Override
    public MenuResponse handle(UssdSession session, String input) {
        if ("0".equals(input)) {
            session.setState(MenuState.LOG_SPRAY_CROP);
            session.put("chemicalPage", "0");
            return ResponseBuilder.cont(LogSprayCropHandler.cropMenuText());
        }

        String crop = session.getString("crop");
        int page = session.getInt("chemicalPage", 0);
        List<ChemicalSummary> all = chemicalService.listForCrop(crop);

        if (all.isEmpty()) {
            return ResponseBuilder.cont("No chemicals found for " + crop + ".\n0. Back");
        }

        int totalPages = (int) Math.ceil(all.size() / (double) PAGE_SIZE);
        int from = page * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, all.size());
        List<ChemicalSummary> pageItems = all.subList(from, to);
        boolean hasNextPage = (page + 1) < totalPages;

        if ("5".equals(input) && hasNextPage) {
            session.put("chemicalPage", String.valueOf(page + 1));
            return handle(session, "");
        }

        if (input != null && !input.isBlank() && !"5".equals(input)) {
            int choice = parseChoice(input, pageItems.size());
            if (choice < 1) {
                return ResponseBuilder.cont(buildChemicalMenu(pageItems, hasNextPage));
            }
            ChemicalSummary chosen = pageItems.get(choice - 1);
            session.put("chemicalId", chosen.id().toString());
            session.put("chemicalName", chosen.name());
            session.put("halfLifeDays", String.valueOf(chosen.halfLifeDays()));
            session.put("phiDays", String.valueOf(chosen.phiDays()));
            session.setState(MenuState.LOG_SPRAY_QUANTITY);
            return ResponseBuilder.cont("Enter quantity sprayed in millilitres (e.g. 500):");
        }

        return ResponseBuilder.cont(buildChemicalMenu(pageItems, hasNextPage));
    }

    private String buildChemicalMenu(List<ChemicalSummary> items, boolean hasNextPage) {
        StringBuilder sb = new StringBuilder("Select chemical applied:\n");
        for (int i = 0; i < items.size(); i++) {
            sb.append(i + 1).append(". ").append(items.get(i).name()).append("\n");
        }
        if (hasNextPage) sb.append("5. More options...\n");
        sb.append("0. Back");
        return sb.toString();
    }

    private int parseChoice(String input, int max) {
        try {
            int n = Integer.parseInt(input.trim());
            return (n >= 1 && n <= max) ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
