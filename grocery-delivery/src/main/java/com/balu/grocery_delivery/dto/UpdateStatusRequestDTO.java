package com.balu.grocery_delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateStatusRequestDTO {

    @NotBlank
    private String status;
}
