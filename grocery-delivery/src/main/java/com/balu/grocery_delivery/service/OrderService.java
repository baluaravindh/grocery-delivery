package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.dto.*;
import com.balu.grocery_delivery.entity.*;
import com.balu.grocery_delivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // placeOrder
    @Transactional
    public OrderResponseDTO placeOrder(Long customerId, OrderRequestDTO dto) {
        // Validate customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));

        // Find customer's cart — throw if no cart
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found."));

        // Check cart is not empty — throw if empty
        if (cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart items not found.");
        }

        // Create Order object — set customer, deliveryAddress, deliverySlot
        Order order = new Order();
        order.setCustomer(customer);
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setDeliverySlot(Order.DeliverySlot.valueOf(dto.getDeliverySlot()));

        //  Loop through each CartItem:
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            //  a. Check stock still available
            Item item = cartItem.getItem();
            if (!item.isAvailable()) {
                throw new RuntimeException("Item not available.");
            }

            if (cartItem.getQuantity() > item.getStockQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + item.getName());
            }

            //  b. Create OrderItem — snapshot itemId, itemName, price, quantity
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setItemId(item.getId());
            newOrderItem.setItemName(item.getName());
            newOrderItem.setPriceAtPurchase(item.getPrice());
            newOrderItem.setQuantity(cartItem.getQuantity());

            //  c. Add OrderItem to Order
            newOrderItem.setOrder(order);
            order.getOrderItems().add(newOrderItem);

            //  d. Deduct stock from Item
            item.setStockQuantity(item.getStockQuantity() - cartItem.getQuantity());
            itemRepository.save(item);

            //  e. Add subtotal to totalAmount
            BigDecimal subtotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(newOrderItem.getQuantity()));

            totalPrice = totalPrice.add(subtotal);
        }
        // Set totalAmount on Order
        order.setTotalAmount(totalPrice);

        // Save Order
        Order saved = orderRepository.save(order);

        orderRepository.flush();

        // Clear cart items after order placed
        cartItemRepository.deleteAllByCartId(cart.getId());

        // Return OrderResponseDTO
        return mapToDto(saved);
    }

    // getOrderById
    public OrderResponseDTO getOrderById(Long orderId) {
//        Customer customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));
        return mapToDto(order);
    }

    // cancelOrder
    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        //  Find customer and order — throw if not found
//        Customer customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Customer not found  with id: " + customerId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));

        //  Check status is PENDING — only PENDING can be cancelled
        if (order.getStatus().equals(Order.OrderStatus.DELIVERED)) {
            throw new RuntimeException("Cannot cancel a delivered order.");
        }
        if (order.getStatus().equals(Order.OrderStatus.CANCELLED)) {
            throw new RuntimeException("Order already cancelled.");
        }

        //  Set status to CANCELLED
        order.setStatus(Order.OrderStatus.CANCELLED);

        //  Restore stock for each OrderItem
        for (OrderItem orderItem : order.getOrderItems()) {
            Item item = itemRepository.findById(orderItem.getItemId())
                    .orElseThrow(() -> new RuntimeException(
                            "Item not found with id " + orderItem.getItemId()));
            item.setStockQuantity(item.getStockQuantity() + orderItem.getQuantity());
            itemRepository.save(item);
        }
        //  Save and return
        Order updated = orderRepository.save(order);
        return mapToDto(updated);
    }

    //  updateOrderStatus (ADMIN only)
    @Transactional
    public OrderResponseDTO updateOrder(Long orderId, UpdateStatusRequestDTO dto) {
        //  Find order — throw if not found
//        Customer customer = customerRepository.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Customer not found  with id: " + customerId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));

        //  Set new status
        if (order.getStatus().equals(Order.OrderStatus.CANCELLED)) {
            throw new RuntimeException("Cannot change the cancelled order.");
        }
        if (order.getStatus().equals(Order.OrderStatus.DELIVERED)) {
            throw new RuntimeException("Order already delivered.");
        }
        order.setStatus(Order.OrderStatus.valueOf(dto.getStatus().toUpperCase()));
        //  Save and return
        Order updated = orderRepository.save(order);
        return mapToDto(updated);
    }

    //  Pagination
    public PagedResponseDTO<OrderResponseDTO> getOrdersByCustomer(
            Long customerId,
            int page,
            int size,
            String sortBy,
            String direction) {

        // Validate customer
        customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // Build pageable
        Sort.Direction sortDir = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));

        // Fetch paginated orders
        Page<Order> orderPage = orderRepository.findByCustomerId(customerId, pageable);

        // Map to DTO
        List<OrderResponseDTO> content = orderPage.getContent()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PagedResponseDTO<>(
                content,
                orderPage.getNumber(),
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.isLast()
        );
    }

    //---MAPPER---
    private OrderResponseDTO mapToDto(Order order) {
        List<OrderItemResponseDTO> itemDtos = order.getOrderItems()
                .stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getItemId(),
                        item.getItemName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getPriceAtPurchase()
                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                )).collect(Collectors.toList());

        return new OrderResponseDTO(
                order.getId(),
                order.getCustomer().getId(),
                order.getCustomer().getFullName(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getDeliveryAddress(),
                order.getDeliverySlot().name(),
                order.getCreatedAt(),
                itemDtos
        );
    }
}
