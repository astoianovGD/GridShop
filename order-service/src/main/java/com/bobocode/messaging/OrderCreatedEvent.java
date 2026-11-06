package com.bobocode.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Event published to Kafka by order-service when a user creates a new order from their bucket.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    /**
     * ID of the newly created order.
     */
    private Long orderId;

    /**
     * ID of the user who placed the order.
     */
    private Long userId;

    /**
     * List of purchased product IDs in the order.
     */
    private List<Long> productIds;
}
