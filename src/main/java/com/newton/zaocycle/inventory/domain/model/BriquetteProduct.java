package com.newton.zaocycle.inventory.domain.model;

import com.newton.zaocycle.shared.infrastructure.id.IdGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class BriquetteProduct {

    private final UUID id;
    private final String sku;
    private final String name;
    private final String description;
    private final BigDecimal weightKg;
    private final BigDecimal unitPrice;
    private final String imageUrl;
    private final int sortOrder;
    private final Instant createdAt;
    private boolean active;
    private Instant updatedAt;

    public BriquetteProduct(UUID id, String sku, String name, String description,
                            BigDecimal weightKg, BigDecimal unitPrice, String imageUrl,
                            boolean active, int sortOrder, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.weightKg = weightKg;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.active = active;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BriquetteProduct create(String sku, String name, String description,
                                          BigDecimal weightKg, BigDecimal unitPrice,
                                          String imageUrl, int sortOrder) {
        Instant now = Instant.now();
        return new BriquetteProduct(IdGenerator.generate(), sku, name, description,
                weightKg, unitPrice, imageUrl, true, sortOrder, now, now);
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Instant.now();
    }

    public UUID id() {
        return id;
    }

    public String sku() {
        return sku;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public BigDecimal weightKg() {
        return weightKg;
    }

    public BigDecimal unitPrice() {
        return unitPrice;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public int sortOrder() {
        return sortOrder;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
