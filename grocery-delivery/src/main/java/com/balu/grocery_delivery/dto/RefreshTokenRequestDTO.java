package com.balu.grocery_delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RefreshTokenRequestDTO {

    @NotBlank(message = "Refresh token is required.")
    private String refreshToken;
}
