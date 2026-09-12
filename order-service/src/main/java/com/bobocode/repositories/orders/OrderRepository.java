package com.bobocode.repositories.orders;

import com.bobocode.entities.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link Order} entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders placed by a specific user.
     *
     * @param userId the user ID
     * @return a list of orders
     */
    List<Order> findAllByUserId(Long userId);
}
