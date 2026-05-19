package com.newton.zaocycle.inventory.application;

import com.newton.zaocycle.inventory.application.command.PlaceOrderCommand;
import com.newton.zaocycle.inventory.application.event.BuyerOrderPlaced;
import com.newton.zaocycle.inventory.domain.model.BriquetteProduct;
import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;
import com.newton.zaocycle.inventory.domain.port.BriquetteBatchRepository;
import com.newton.zaocycle.inventory.domain.port.BuyerOrderRepository;
import com.newton.zaocycle.shared.exception.NotFoundException;
import com.newton.zaocycle.shared.exception.ValidationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
class OrderServiceImpl implements OrderService {

    private final BuyerOrderRepository orderRepository;
    private final BriquetteBatchRepository batchRepository;
    private final ProductService productService;
    private final ApplicationEventPublisher eventPublisher;

    OrderServiceImpl(BuyerOrderRepository orderRepository,
                     BriquetteBatchRepository batchRepository,
                     ProductService productService,
                     ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.batchRepository = batchRepository;
        this.productService = productService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public BuyerOrder placeOrder(PlaceOrderCommand cmd) {
        BriquetteProduct product = productService.findById(cmd.productId());
        if (!product.isActive()) {
            throw new ValidationException("Product is no longer available: " + product.sku());
        }

        BigDecimal totalKg = product.weightKg().multiply(BigDecimal.valueOf(cmd.quantity()));
        BigDecimal totalAmount = product.unitPrice().multiply(BigDecimal.valueOf(cmd.quantity()));

        BigDecimal available = batchRepository.sumRemaining();
        if (available.compareTo(totalKg) < 0) {
            throw new ValidationException("Insufficient stock. Available: " + available + " kg, requested: " + totalKg + " kg");
        }

        BuyerOrder order = BuyerOrder.create(
                cmd.buyerId(), cmd.productId(), cmd.quantity(),
                product.unitPrice(), totalKg, totalAmount,
                cmd.deliveryAddress(), cmd.deliveryPhone(),
                cmd.requestedDelivery(), cmd.notes());

        order = orderRepository.save(order);
        eventPublisher.publishEvent(new BuyerOrderPlaced(
                order.id(), order.buyerId(), order.productId(),
                order.totalAmount(), order.totalKg()));
        return order;
    }

    @Override
    public BuyerOrder findByIdAndBuyer(UUID orderId, UUID buyerId) {
        BuyerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        if (!order.buyerId().equals(buyerId)) {
            throw new NotFoundException("Order not found: " + orderId);
        }
        return order;
    }

    @Override
    public Page<BuyerOrder> findForBuyer(UUID buyerId, Pageable pageable) {
        return orderRepository.findByBuyerId(buyerId, pageable);
    }

    @Override
    @Transactional
    public BuyerOrder cancelByBuyer(UUID orderId, UUID buyerId) {
        BuyerOrder order = findByIdAndBuyer(orderId, buyerId);
        order.cancel();
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public BuyerOrder markReadyForDelivery(UUID orderId) {
        BuyerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        order.markReadyForDelivery();
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public BuyerOrder markDelivered(UUID orderId) {
        BuyerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        order.markDelivered();
        return orderRepository.save(order);
    }

    @Override
    public Page<BuyerOrder> findAll(OrderStatus status, Pageable pageable) {
        if (status == null) {
            return orderRepository.findAll(pageable);
        }
        return orderRepository.findByStatus(status, pageable);
    }
}
