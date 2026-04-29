package com.balu.grocery_delivery.repository;

import com.balu.grocery_delivery.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id=:cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
