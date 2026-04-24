package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.dto.*;
import com.balu.grocery_delivery.entity.Customer;
import com.balu.grocery_delivery.entity.RefreshToken;
import com.balu.grocery_delivery.exception.DuplicateUserFoundException;
import com.balu.grocery_delivery.exception.InvalidCredentialsException;
import com.balu.grocery_delivery.exception.ResourceNotFoundException;
import com.balu.grocery_delivery.repository.CustomerRepository;
import com.balu.grocery_delivery.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // REGISTER
    public CustomerResponseDTO register(RegisterRequestDTO dto) {

        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserFoundException("Email already registered: " + dto.getEmail());
        }

        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(encoder.encode(dto.getPassword()));
        customer.setPhone(dto.getPhone());
        customer.setRole(Customer.Role.CUSTOMER);

        Customer saved = customerRepository.save(customer);
        return mapToDto(saved);
    }

    // LOGIN
    public LoginResponseDTO login(LoginRequestDTO dto) {

        Customer customer = customerRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with email: " + dto.getEmail()));

        // Compare raw password with encrypted password
        if (!encoder.matches(dto.getPassword(), customer.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Generate JWT token
        String accessToken = jwtUtil.generateToken(customer.getEmail(), customer.getRole().name());

        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(customer.getId());

        return new LoginResponseDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getRole().name(),
                accessToken,
                "Bearer ",
                refreshToken.getToken()
        );
    }

    // Change Password
    public void changePassword(String email, ChangePasswordRequestDTO dto) {

        // Step 1: Find user by email (extracted from JWT)
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));

        // Step 2: Verify current password is correct
        if (!encoder.matches(dto.getOldPassword(), customer.getPassword())) {
            throw new InvalidCredentialsException("Current Password is incorrect");
        }

        // Step 3: Check new password and confirm password match
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Step 4: Check new password is different from current
        if (encoder.matches(dto.getNewPassword(), customer.getPassword())) {
            throw new RuntimeException("New password must be different from current password");
        }

        // Step 5: Encode and save new password
        customer.setPassword(encoder.encode(dto.getNewPassword()));
        customerRepository.save(customer);

        // Step 6: Invalidate all refresh tokens — force re-login on all devices
        refreshTokenService.deleteAllRefreshTokens(customer.getId());
    }

    private CustomerResponseDTO mapToDto(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getRole().name(),
                customer.getCreatedAt()
        );
    }
}
