package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.application.command.PlaceOrderCommand;
import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    BuyerOrder placeOrder(PlaceOrderCommand cmd);
    BuyerOrder findByIdAndBuyer(UUID orderId, UUID buyerId);
    Page<BuyerOrder> findForBuyer(UUID buyerId, Pageable pageable);
    BuyerOrder cancelByBuyer(UUID orderId, UUID buyerId);
    BuyerOrder markReadyForDelivery(UUID orderId);
    BuyerOrder markDelivered(UUID orderId);
    Page<BuyerOrder> findAll(OrderStatus status, Pageable pageable);
}
