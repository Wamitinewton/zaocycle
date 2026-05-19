package com.newton.zaocycle.inventory.api;

import com.newton.zaocycle.auth.domain.model.AuthenticatedPrincipal;
import com.newton.zaocycle.inventory.api.dto.OrderResponse;
import com.newton.zaocycle.inventory.api.dto.PlaceOrderRequest;
import com.newton.zaocycle.inventory.application.OrderService;
import com.newton.zaocycle.inventory.application.command.PlaceOrderCommand;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/buyer/orders")
@PreAuthorize("hasRole('BUYER')")
public class BuyerOrderController {

    private final OrderService orderService;

    public BuyerOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@Valid @RequestBody PlaceOrderRequest request,
                                     @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        PlaceOrderCommand cmd = new PlaceOrderCommand(
                principal.id(), request.productId(), request.quantity(),
                request.deliveryAddress(), request.deliveryPhone(),
                request.requestedDelivery(), request.notes());
        return OrderResponse.from(orderService.placeOrder(cmd));
    }

    @GetMapping
    public Page<OrderResponse> myOrders(@AuthenticationPrincipal AuthenticatedPrincipal principal,
                                         @PageableDefault(size = 20) Pageable pageable) {
        return orderService.findForBuyer(principal.id(), pageable).map(OrderResponse::from);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id,
                                   @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return OrderResponse.from(orderService.findByIdAndBuyer(id, principal.id()));
    }

    @DeleteMapping("/{id}")
    public OrderResponse cancelOrder(@PathVariable UUID id,
                                      @AuthenticationPrincipal AuthenticatedPrincipal principal) {
        return OrderResponse.from(orderService.cancelByBuyer(id, principal.id()));
    }
}
