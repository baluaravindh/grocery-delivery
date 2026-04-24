package com.balu.grocery_delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private LocalDateTime createdAt;
}
