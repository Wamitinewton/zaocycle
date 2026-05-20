package com.newton.zaocycle.chemical.domain.model;

import java.util.UUID;

public final class Chemical {

    private final UUID id;
    private final String name;
    private final String activeIngredient;
    private final ChemicalCategory category;
    private final int halfLifeDays;
    private final int phiDays;
    private final String commonCrops;
    private final boolean active;

    public Chemical(UUID id, String name, String activeIngredient, ChemicalCategory category,
                    int halfLifeDays, int phiDays, String commonCrops, boolean active) {
        this.id = id;
        this.name = name;
        this.activeIngredient = activeIngredient;
        this.category = category;
        this.halfLifeDays = halfLifeDays;
        this.phiDays = phiDays;
        this.commonCrops = commonCrops;
        this.active = active;
    }

    public UUID id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String activeIngredient() {
        return activeIngredient;
    }

    public ChemicalCategory category() {
        return category;
    }

    public int halfLifeDays() {
        return halfLifeDays;
    }

    public int phiDays() {
        return phiDays;
    }

    public String commonCrops() {
        return commonCrops;
    }

    public boolean active() {
        return active;
    }
}
