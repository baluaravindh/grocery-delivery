package com.balu.grocery_delivery.dto;

import com.balu.grocery_delivery.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {

    private Long orderId;
    private Long customerId;
    private String customerName;
    private String status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String deliverySlot;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDTO> items;
}
