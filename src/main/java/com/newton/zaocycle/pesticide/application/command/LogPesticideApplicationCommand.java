package com.newton.zaocycle.pesticide.application.command;

import java.util.UUID;

public record LogPesticideApplicationCommand(
        UUID farmerId,
        UUID chemicalId,
        String crop,
        Double quantityMl
) {
}
