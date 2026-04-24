package com.balu.grocery_delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private String tokenType;
    private String refreshToken;
}
