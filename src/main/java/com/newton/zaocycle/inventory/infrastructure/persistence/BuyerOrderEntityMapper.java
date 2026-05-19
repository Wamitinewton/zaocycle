package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;

final class BuyerOrderEntityMapper {

    private BuyerOrderEntityMapper() {}

    static BuyerOrder toDomain(BuyerOrderEntity e) {
        return new BuyerOrder(
                e.getId(), e.getBuyerId(), e.getProductId(),
                e.getQuantity(), e.getUnitPrice(), e.getTotalKg(), e.getTotalAmount(),
                e.getDeliveryAddress(), e.getDeliveryPhone(), e.getRequestedDelivery(),
                e.getDeliveredAt(), OrderStatus.valueOf(e.getStatus()),
                e.getMpesaTransactionId(), e.getNotes(),
                e.getCreatedAt(), e.getUpdatedAt()
        );
    }

    static BuyerOrderEntity toEntity(BuyerOrder domain) {
        BuyerOrderEntity e = new BuyerOrderEntity();
        e.setId(domain.id());
        e.setBuyerId(domain.buyerId());
        e.setProductId(domain.productId());
        e.setQuantity(domain.quantity());
        e.setUnitPrice(domain.unitPrice());
        e.setTotalKg(domain.totalKg());
        e.setTotalAmount(domain.totalAmount());
        e.setDeliveryAddress(domain.deliveryAddress());
        e.setDeliveryPhone(domain.deliveryPhone());
        e.setRequestedDelivery(domain.requestedDelivery());
        e.setDeliveredAt(domain.deliveredAt());
        e.setStatus(domain.status().name());
        e.setMpesaTransactionId(domain.mpesaTransactionId());
        e.setNotes(domain.notes());
        return e;
    }
}
