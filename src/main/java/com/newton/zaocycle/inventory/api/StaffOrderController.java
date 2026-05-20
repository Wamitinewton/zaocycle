package com.newton.zaocycle.inventory.api;

import com.newton.zaocycle.inventory.api.dto.OrderResponse;
import com.newton.zaocycle.inventory.application.OrderService;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard/orders")
@PreAuthorize("hasAnyRole('COOP_MANAGER','ADMIN')")
public class StaffOrderController {

    private final OrderService orderService;

    public StaffOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Page<OrderResponse> listAll(@RequestParam(required = false) OrderStatus status,
                                       @PageableDefault(size = 20) Pageable pageable) {
        return orderService.findAll(status, pageable).map(OrderResponse::from);
    }

    @PostMapping("/{id}/ready")
    public OrderResponse markReady(@PathVariable UUID id) {
        return OrderResponse.from(orderService.markReadyForDelivery(id));
    }

    @PostMapping("/{id}/deliver")
    public OrderResponse markDelivered(@PathVariable UUID id) {
        return OrderResponse.from(orderService.markDelivered(id));
    }
}
