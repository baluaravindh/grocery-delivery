package com.balu.grocery_delivery.controller;

import com.balu.grocery_delivery.dto.CartItemRequestDTO;
import com.balu.grocery_delivery.dto.CartResponseDTO;
import com.balu.grocery_delivery.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // POST /api/cart/{customerId}/items
    @PostMapping("{customerId}/items")
    public ResponseEntity<CartResponseDTO> addCart(@PathVariable Long customerId,
                                                   @RequestBody @Valid CartItemRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.addToCard(customerId, dto));
    }

    // GET /api/cart/{customerId}
    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponseDTO> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    // PUT /api/cart/{customerId}/items/{itemId}
    @PutMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> updateCart(
            @PathVariable Long customerId,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCart(customerId, itemId, quantity));
    }

    // DELETE /api/cart/{customerId}/items/{itemId}
    @DeleteMapping("{customerId}/items/{itemId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(
            @PathVariable Long customerId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeCart(customerId, itemId));
    }

    // DELETE /api/cart/{customerId}
    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> clearCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.clearCart(customerId));
    }
}
