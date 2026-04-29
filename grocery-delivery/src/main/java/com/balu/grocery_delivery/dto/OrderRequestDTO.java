package com.balu.grocery_delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequestDTO {

    @NotBlank
    private String deliveryAddress;

    @NotNull
    private String deliverySlot;
}
