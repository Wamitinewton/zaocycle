package com.newton.zaocycle.scheduling.domain.model;

public enum JobGroup {
    HARVEST("harvest"),
    PICKUP_REMINDER("pickup-reminder"),
    PICKUP_EXPIRY("pickup-expiry");

    private final String value;

    JobGroup(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
