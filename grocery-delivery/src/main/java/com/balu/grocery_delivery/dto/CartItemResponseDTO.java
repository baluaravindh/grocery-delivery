package com.balu.grocery_delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {

    private Long cartItemId;
    private Long itemId;
    private String itemName;
    private String category;
    private String unit;
    private Integer quantity;
    private BigDecimal priceAtAdd;
    private BigDecimal subTotal;
}
