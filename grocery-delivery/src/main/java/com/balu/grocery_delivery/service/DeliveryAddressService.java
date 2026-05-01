package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.dto.DeliveryAddressRequestDto;
import com.balu.grocery_delivery.dto.DeliveryAddressResponseDTO;
import com.balu.grocery_delivery.entity.Customer;
import com.balu.grocery_delivery.entity.DeliveryAddress;
import com.balu.grocery_delivery.exception.ResourceNotFoundException;
import com.balu.grocery_delivery.repository.CustomerRepository;
import com.balu.grocery_delivery.repository.DeliveryAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final CustomerRepository customerRepository;

    // addAddress:
    public DeliveryAddressResponseDTO addAddress(Long customerId, DeliveryAddressRequestDto dto) {
        // 1. Validate customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // 2. Create DeliveryAddress — set all fields + link to customer
        DeliveryAddress deliveryAddress = new DeliveryAddress();
        deliveryAddress.setStreet(dto.getStreet());
        deliveryAddress.setCity(dto.getCity());
        deliveryAddress.setState(dto.getState());
        deliveryAddress.setPincode(dto.getPincode());
        deliveryAddress.setAddressType(dto.getAddressType());
        deliveryAddress.setDefault(dto.isDefault());
        deliveryAddress.setCustomer(customer);

        // 3. Save and return DTO
        DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);
        return mapToDto(saved);
    }

    // getAddressesByCustomer:
    public List<DeliveryAddressResponseDTO> getAddressesByCustomer(Long customerId) {
        // 1. Validate customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        // 2. Return all addresses mapped to DTO
        return deliveryAddressRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // deleteAddress:
    public void deleteAddress(Long id) {
        // 1. Find address by id — throw if not found
        DeliveryAddress address = deliveryAddressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + id));
        // 2. Delete
        // 3. Return success message
        deliveryAddressRepository.delete(address);
    }

    //---MAPPER---
    private DeliveryAddressResponseDTO mapToDto(DeliveryAddress deliveryAddress) {
        return new DeliveryAddressResponseDTO(
                deliveryAddress.getId(),
                deliveryAddress.getStreet(),
                deliveryAddress.getCity(),
                deliveryAddress.getState(),
                deliveryAddress.getPincode(),
                deliveryAddress.getAddressType(),
                deliveryAddress.isDefault(),
                deliveryAddress.getCustomer().getId()
        );
    }
}
