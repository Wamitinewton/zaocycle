package com.newton.zaocycle.chemical.application;

import com.newton.zaocycle.chemical.application.dto.ChemicalSummary;
import com.newton.zaocycle.chemical.domain.model.Chemical;

import java.util.List;
import java.util.UUID;

public interface ChemicalService {
    List<Chemical> listAllActive();

    List<ChemicalSummary> listForCrop(String crop);

    Chemical getById(UUID id);
}
