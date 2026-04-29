package com.balu.grocery_delivery.controller;

import com.balu.grocery_delivery.dto.*;
import com.balu.grocery_delivery.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    //  POST   /api/orders/{customerId} → placeOrder
    @PostMapping("/{customerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> placeOrder(
            @PathVariable Long customerId, @RequestBody @Valid OrderRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(customerId, dto));
    }

    //  GET    /api/orders/{orderId}    → getOrderById
    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(orderId));
    }

    //  PATCH  /api/orders/{orderId}/cancel → cancelOrder (CUSTOMER)
    @PatchMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    //  PATCH  /api/orders/{orderId}/status → updateOrderStatus (ADMIN)
    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long orderId,
                                                        @RequestBody @Valid UpdateStatusRequestDTO dto) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, dto));
    }

    //  GET    /api/orders/customer/{customerId}    → getOrdersByCustomer (paginated)
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponseDTO<OrderResponseDTO>> getOrdersByCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId, page, size, sortBy, direction));
    }
}
