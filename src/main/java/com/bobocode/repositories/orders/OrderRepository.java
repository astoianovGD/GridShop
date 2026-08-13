package com.bobocode.repositories.orders;

import com.bobocode.entities.orders.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing {@link Order} entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders associated with a given user ID,
     * fetching items and products.
     *
     * @param userId the user ID
     * @return a list of orders belonging to the user
     */
    @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN FETCH o.items i
        LEFT JOIN FETCH i.product
        WHERE o.user.id = :userId
        """)
    List<Order> findAllByUserId(@Param("userId") long userId);
}
