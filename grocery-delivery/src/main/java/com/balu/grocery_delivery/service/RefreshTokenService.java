package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.entity.Customer;
import com.balu.grocery_delivery.entity.RefreshToken;
import com.balu.grocery_delivery.exception.ResourceNotFoundException;
import com.balu.grocery_delivery.repository.CustomerRepository;
import com.balu.grocery_delivery.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomerRepository customerRepository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // CREATE a new refresh token for a user
    @Transactional
    public RefreshToken createRefreshToken(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));

        // Delete any existing refresh token for this user
        refreshTokenRepository.deleteByCustomerId(customerId);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCustomer(customer);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000));
        refreshToken.setRevokes(false);

        return refreshTokenRepository.save(refreshToken);
    }

    // VALIDATE refresh token
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Refresh token not found with id " + token));

        if (refreshToken.isRevokes()) {
            throw new RuntimeException("Refresh token has been revoked.");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token has expired. Please login again.");
        }
        return refreshToken;
    }

    // REVOKE on logout
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Refresh token not found with id " + token));
        refreshToken.setRevokes(true);
        refreshTokenRepository.save(refreshToken);
    }

    // DELETE all tokens for user (force logout all devices)
    @Transactional
    public void deleteAllRefreshTokens(Long customerId) {
        refreshTokenRepository.deleteByCustomerId(customerId);
    }
}
