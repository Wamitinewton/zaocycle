package com.newton.zaocycle.pesticide.api.dto;

import com.newton.zaocycle.pesticide.domain.model.ApplicationStatus;
import com.newton.zaocycle.pesticide.domain.model.PesticideApplication;

import java.time.LocalDate;
import java.util.UUID;

public record PesticideApplicationResponse(
        UUID id,
        UUID farmerId,
        UUID chemicalId,
        String crop,
        Double quantityMl,
        LocalDate safeHarvestDate,
        ApplicationStatus status
) {
    public static PesticideApplicationResponse from(PesticideApplication a) {
        return new PesticideApplicationResponse(
                a.id(), a.farmerId(), a.chemicalId(), a.crop(),
                a.quantityMl(), a.safeHarvestDate(), a.status()
        );
    }
}
