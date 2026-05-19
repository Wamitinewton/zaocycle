package com.newton.zaocycle.inventory.application.command;

import java.time.LocalDate;
import java.util.UUID;

public record PlaceOrderCommand(
        UUID buyerId,
        UUID productId,
        int quantity,
        String deliveryAddress,
        String deliveryPhone,
        LocalDate requestedDelivery,
        String notes
) {}
