package com.newton.zaocycle.inventory.api.dto;

import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID buyerId,
        UUID productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalKg,
        BigDecimal totalAmount,
        String deliveryAddress,
        String deliveryPhone,
        LocalDate requestedDelivery,
        Instant deliveredAt,
        OrderStatus status,
        UUID mpesaTransactionId,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static OrderResponse from(BuyerOrder o) {
        return new OrderResponse(
                o.id(), o.buyerId(), o.productId(),
                o.quantity(), o.unitPrice(), o.totalKg(), o.totalAmount(),
                o.deliveryAddress(), o.deliveryPhone(), o.requestedDelivery(),
                o.deliveredAt(), o.status(), o.mpesaTransactionId(),
                o.notes(), o.createdAt(), o.updatedAt()
        );
    }
}
