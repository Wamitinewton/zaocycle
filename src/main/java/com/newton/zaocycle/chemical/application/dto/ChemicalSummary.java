package com.newton.zaocycle.chemical.application.dto;

import java.util.UUID;

public record ChemicalSummary(UUID id, String name, int halfLifeDays, int phiDays) {}
