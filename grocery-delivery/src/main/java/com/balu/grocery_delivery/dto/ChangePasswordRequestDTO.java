package com.balu.grocery_delivery.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequestDTO {

    @NotBlank(message = "Current password is required.")
    private String oldPassword;

    @NotBlank(message = "New Password is required.")
    @Size(min = 6, message = "New password must be at least 6 characters.")
    private String newPassword;

    @NotBlank(message = "Confirm New Password is required.")
    private String confirmNewPassword;
}
