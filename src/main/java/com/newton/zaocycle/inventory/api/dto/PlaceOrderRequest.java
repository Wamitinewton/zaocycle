package com.newton.zaocycle.inventory.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record PlaceOrderRequest(
        @NotNull UUID productId,
        @Min(1) int quantity,
        @NotBlank String deliveryAddress,
        @NotBlank String deliveryPhone,
        LocalDate requestedDelivery,
        String notes
) {}
