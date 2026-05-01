package com.balu.grocery_delivery.controller;

import com.balu.grocery_delivery.dto.DeliveryAddressRequestDto;
import com.balu.grocery_delivery.dto.DeliveryAddressResponseDTO;
import com.balu.grocery_delivery.service.DeliveryAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class DeliveryAddressController {

    private final DeliveryAddressService deliveryAddressService;

    //  POST   /api/customers/{customerId}/addresses
    @PostMapping("/{customerId}/addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DeliveryAddressResponseDTO> addAddress(
            @PathVariable Long customerId,
            @RequestBody @Valid DeliveryAddressRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryAddressService.addAddress(customerId, dto));
    }

    //  GET    /api/customers/{customerId}/addresses
    @GetMapping("/{customerId}/addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DeliveryAddressResponseDTO>> getAddressesByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(deliveryAddressService.getAddressesByCustomer(customerId));
    }

    //  DELETE /api/customers/{customerId}/addresses/{addressId}
    @DeleteMapping("/{customerId}/addresses/{addressId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> deleteAddress(@PathVariable Long customerId,
                                                @PathVariable Long addressId) {
        deliveryAddressService.deleteAddress(addressId);
        return ResponseEntity.ok("Deleted Address Successfully");
    }
}
