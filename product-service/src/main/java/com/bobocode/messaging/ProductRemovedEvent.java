package com.bobocode.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event published to Kafka when a product is deactivated/removed from the marketplace.
 * Consumed by order-service to clean up deactivated products from active user buckets.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRemovedEvent {

    /**
     * ID of the removed product.
     */
    private Long productId;

    /**
     * Name of the removed product.
     */
    private String productName;

    /**
     * Epoch millisecond timestamp when the product was removed.
     */
    private long removedAtTimestamp;
}
