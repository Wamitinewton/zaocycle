package com.newton.zaocycle.inventory.infrastructure.persistence;

import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;
import com.newton.zaocycle.inventory.domain.port.BuyerOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class BuyerOrderRepositoryAdapter implements BuyerOrderRepository {

    private final BuyerOrderJpaRepository jpa;

    BuyerOrderRepositoryAdapter(BuyerOrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<BuyerOrder> findById(UUID id) {
        return jpa.findById(id).map(BuyerOrderEntityMapper::toDomain);
    }

    @Override
    public Page<BuyerOrder> findByBuyerId(UUID buyerId, Pageable pageable) {
        return jpa.findByBuyerId(buyerId, pageable).map(BuyerOrderEntityMapper::toDomain);
    }

    @Override
    public Page<BuyerOrder> findAll(Pageable pageable) {
        return jpa.findAll(pageable).map(BuyerOrderEntityMapper::toDomain);
    }

    @Override
    public Page<BuyerOrder> findByStatus(OrderStatus status, Pageable pageable) {
        return jpa.findByStatus(status.name(), pageable).map(BuyerOrderEntityMapper::toDomain);
    }

    @Override
    public BuyerOrder save(BuyerOrder order) {
        BuyerOrderEntity entity = BuyerOrderEntityMapper.toEntity(order);
        return BuyerOrderEntityMapper.toDomain(jpa.save(entity));
    }
}
