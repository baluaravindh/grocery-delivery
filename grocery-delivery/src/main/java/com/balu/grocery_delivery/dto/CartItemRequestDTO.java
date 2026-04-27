package com.balu.grocery_delivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDTO {

    @NotNull
    private Long itemId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
