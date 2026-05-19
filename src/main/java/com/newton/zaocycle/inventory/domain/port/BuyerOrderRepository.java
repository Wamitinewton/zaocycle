package com.newton.zaocycle.inventory.domain.port;

import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BuyerOrderRepository {
    Optional<BuyerOrder> findById(UUID id);
    Page<BuyerOrder> findByBuyerId(UUID buyerId, Pageable pageable);
    Page<BuyerOrder> findAll(Pageable pageable);
    Page<BuyerOrder> findByStatus(OrderStatus status, Pageable pageable);
    BuyerOrder save(BuyerOrder order);
}
