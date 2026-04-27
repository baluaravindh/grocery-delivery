package com.balu.grocery_delivery.service;

import com.balu.grocery_delivery.dto.CartItemRequestDTO;
import com.balu.grocery_delivery.dto.CartItemResponseDTO;
import com.balu.grocery_delivery.dto.CartResponseDTO;
import com.balu.grocery_delivery.entity.Cart;
import com.balu.grocery_delivery.entity.CartItem;
import com.balu.grocery_delivery.entity.Customer;
import com.balu.grocery_delivery.entity.Item;
import com.balu.grocery_delivery.exception.ResourceNotFoundException;
import com.balu.grocery_delivery.repository.CartItemRepository;
import com.balu.grocery_delivery.repository.CartRepository;
import com.balu.grocery_delivery.repository.CustomerRepository;
import com.balu.grocery_delivery.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public CartResponseDTO addToCard(Long customerId, CartItemRequestDTO dto) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + dto.getItemId()));

        if (!item.isAvailable()) {
            throw new ResourceNotFoundException("Item is not available: " + item.getName());
        }

        if (dto.getQuantity() > item.getStockQuantity()) {
            throw new RuntimeException("Insufficient stock for: " + item.getName() +
                    ". Available: " + item.getStockQuantity());
        }

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customer);
                    return cartRepository.save(newCart);
                });

        cartItemRepository.findByCartIdAndItemId(cart.getId(), dto.getItemId())
                .ifPresentOrElse(existingItem -> {
                            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
                            cartItemRepository.save(existingItem);
                        },
                        () -> {
                            CartItem newCartItem = new CartItem();
                            newCartItem.setCart(cart);
                            newCartItem.setItem(item);
                            newCartItem.setQuantity(dto.getQuantity());
                            newCartItem.setPriceAtAdd(item.getPrice());
                            cartItemRepository.save(newCartItem);
                        });
        return getCart(customerId);
    }

    public CartResponseDTO getCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for customer: " + customerId));
        return mapToDto(cart);
    }

    @Transactional
    public CartResponseDTO updateCart(Long customerId, Long itemId, int quantity) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for customer: " + customerId));

        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart."));

        if (quantity > cartItem.getItem().getStockQuantity()) {
            throw new RuntimeException("Insufficient stock Available: " +
                    cartItem.getItem().getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return getCart(customerId);
    }

    @Transactional
    public CartResponseDTO removeCart(Long customerId, Long itemId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for customer: " + customerId));

        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(
                cart.getId(), itemId).orElseThrow(
                () -> new ResourceNotFoundException("Item not found in cart.")
        );

        cartItemRepository.delete(cartItem);
        return getCart(customerId);
    }

    @Transactional
    public String clearCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("No cart found for customer: " + customerId));
        cartItemRepository.deleteAll(cart.getCartItems());
        return "Cart cleared successfully";
    }

    private CartResponseDTO mapToDto(Cart cart) {

        BigDecimal total = BigDecimal.ZERO;

        List<CartItemResponseDTO> itemDTOs = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            BigDecimal subtotal = cartItem.getPriceAtAdd()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            itemDTOs.add(new CartItemResponseDTO(
                    cartItem.getId(),
                    cartItem.getItem().getId(),
                    cartItem.getItem().getName(),
                    cartItem.getItem().getCategory(),
                    cartItem.getItem().getUnit(),
                    cartItem.getQuantity(),
                    cartItem.getPriceAtAdd(),
                    subtotal
            ));
        }

        return new CartResponseDTO(
                cart.getId(),
                cart.getCustomer().getId(),
                cart.getCustomer().getFullName(),
                itemDTOs,
                total,
                itemDTOs.size(),
                cart.getCreatedAt()
        );
    }
}
