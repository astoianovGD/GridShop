package com.bobocode.repositories.orders;

import com.bobocode.entities.orders.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link OrderItem} entities.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Finds all order items associated with a given order ID.
     *
     * @param orderId the order ID
     * @return a list of order items
     */
    List<OrderItem> findAllByOrderId(Long orderId);
}
