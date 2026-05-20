package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.shared.infrastructure.audit.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "buyer_orders")
@Getter
@Setter
@NoArgsConstructor
class BuyerOrderEntity extends AuditableEntity {

    @Id
    private UUID id;

    @Column(name = "buyer_id", nullable = false)
    private UUID buyerId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_kg", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalKg;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_phone", nullable = false, length = 15)
    private String deliveryPhone;

    @Column(name = "requested_delivery")
    private LocalDate requestedDelivery;

    @Column(name = "delivered_at")
    private Instant deliveredAt;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "mpesa_transaction_id")
    private UUID mpesaTransactionId;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
