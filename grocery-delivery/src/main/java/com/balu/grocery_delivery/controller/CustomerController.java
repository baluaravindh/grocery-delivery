package com.balu.grocery_delivery.controller;

import com.balu.grocery_delivery.dto.*;
import com.balu.grocery_delivery.entity.Customer;
import com.balu.grocery_delivery.entity.RefreshToken;
import com.balu.grocery_delivery.security.JwtUtil;
import com.balu.grocery_delivery.service.CustomerService;
import com.balu.grocery_delivery.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<CustomerResponseDTO> register(@Valid @RequestBody RegisterRequestDTO dto) {
        CustomerResponseDTO responseDTO = customerService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(customerService.login(dto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO dto) {

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(dto.getRefreshToken());

        Customer customer = refreshToken.getCustomer();

        String newAccessToken = jwtUtil.generateToken(customer.getEmail(), customer.getRole().name());

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(customer.getId());

        LoginResponseDTO response = new LoginResponseDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getRole().name(),
                newAccessToken,
                "Bearer",
                newRefreshToken.getToken());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequestDTO dto) {
        refreshTokenService.revokeRefreshToken(dto.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    // POST /api/users/change-password
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequestDTO dto) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // getName() returns the email we set as subject in JWT

        customerService.changePassword(email, dto);

        return ResponseEntity.ok("Password changed successfully. Please login again.");
    }
}
