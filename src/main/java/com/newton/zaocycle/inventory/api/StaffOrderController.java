package com.newton.zaocycle.inventory.api;

import com.newton.zaocycle.inventory.api.dto.OrderResponse;
import com.newton.zaocycle.inventory.application.OrderService;
import com.newton.zaocycle.inventory.domain.model.OrderStatus;
import com.newton.zaocycle.shared.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> listAll(
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<OrderResponse> page = orderService.findAll(status, pageable).map(OrderResponse::from);
        return ResponseEntity.ok(ApiResponse.ok(page));
    }

    @PostMapping("/{id}/ready")
    public ResponseEntity<ApiResponse<OrderResponse>> markReady(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(OrderResponse.from(orderService.markReadyForDelivery(id))));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<ApiResponse<OrderResponse>> markDelivered(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(OrderResponse.from(orderService.markDelivered(id))));
    }
}
