package com.balu.grocery_delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {

    private Long cartId;
    private Long customerId;
    private String customerName;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;
    private int totalItems;
    private LocalDateTime createdAt;
}
