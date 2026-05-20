package com.newton.zaocycle.collection.application.command;

import java.math.BigDecimal;

public record CollectPickupCommand(BigDecimal weightKg, byte[] photo, String notes) {
}
