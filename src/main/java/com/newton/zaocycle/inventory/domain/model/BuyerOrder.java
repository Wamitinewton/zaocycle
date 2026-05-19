package com.newton.zaocycle.inventory.domain.model;

import com.newton.zaocycle.shared.exception.DomainException;
import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class BuyerOrder {

    private final UUID id;
    private final UUID buyerId;
    private final UUID productId;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal totalKg;
    private final BigDecimal totalAmount;
    private final String deliveryAddress;
    private final String deliveryPhone;
    private final LocalDate requestedDelivery;
    private Instant deliveredAt;
    private OrderStatus status;
    private UUID mpesaTransactionId;
    private final String notes;
    private final Instant createdAt;
    private Instant updatedAt;

    public BuyerOrder(UUID id, UUID buyerId, UUID productId, int quantity,
                       BigDecimal unitPrice, BigDecimal totalKg, BigDecimal totalAmount,
                       String deliveryAddress, String deliveryPhone, LocalDate requestedDelivery,
                       Instant deliveredAt, OrderStatus status, UUID mpesaTransactionId,
                       String notes, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalKg = totalKg;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.deliveryPhone = deliveryPhone;
        this.requestedDelivery = requestedDelivery;
        this.deliveredAt = deliveredAt;
        this.status = status;
        this.mpesaTransactionId = mpesaTransactionId;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BuyerOrder create(UUID buyerId, UUID productId, int quantity,
                                     BigDecimal unitPrice, BigDecimal totalKg, BigDecimal totalAmount,
                                     String deliveryAddress, String deliveryPhone,
                                     LocalDate requestedDelivery, String notes) {
        Instant now = Instant.now();
        return new BuyerOrder(IdGenerator.generate(), buyerId, productId, quantity,
                unitPrice, totalKg, totalAmount, deliveryAddress, deliveryPhone,
                requestedDelivery, null, OrderStatus.PENDING_PAYMENT, null, notes, now, now);
    }

    public void markPaid(UUID txId) {
        requireStatus(OrderStatus.PENDING_PAYMENT);
        this.status = OrderStatus.PAID;
        this.mpesaTransactionId = txId;
        this.updatedAt = Instant.now();
    }

    public void markReadyForDelivery() {
        requireStatus(OrderStatus.PAID);
        this.status = OrderStatus.READY_FOR_DELIVERY;
        this.updatedAt = Instant.now();
    }

    public void markDelivered() {
        requireStatus(OrderStatus.READY_FOR_DELIVERY);
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        if (status != OrderStatus.PENDING_PAYMENT && status != OrderStatus.PAID) {
            throw new DomainException("Cannot cancel order in status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    private void requireStatus(OrderStatus required) {
        if (status != required) {
            throw new DomainException("Expected order status " + required + " but was " + status);
        }
    }

    public UUID id()                     { return id; }
    public UUID buyerId()                { return buyerId; }
    public UUID productId()              { return productId; }
    public int quantity()                { return quantity; }
    public BigDecimal unitPrice()        { return unitPrice; }
    public BigDecimal totalKg()          { return totalKg; }
    public BigDecimal totalAmount()      { return totalAmount; }
    public String deliveryAddress()      { return deliveryAddress; }
    public String deliveryPhone()        { return deliveryPhone; }
    public LocalDate requestedDelivery() { return requestedDelivery; }
    public Instant deliveredAt()         { return deliveredAt; }
    public OrderStatus status()          { return status; }
    public UUID mpesaTransactionId()     { return mpesaTransactionId; }
    public String notes()                { return notes; }
    public Instant createdAt()           { return createdAt; }
    public Instant updatedAt()           { return updatedAt; }
}
