package com.bobocode.dto.orders;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing an item within an order.
 */
@Data
public class OrderItemDto {
    /**
     * The unique identifier of the order item.
     */
    private long orderItemId;

    /**
     * The unique identifier of the product.
     */
    private long productId;

    /**
     * The name of the product at the time of order.
     */
    private String productName;

    /**
     * The price of the product at the time of purchase.
     */
    private BigDecimal priceAtPurchase;

    /**
     * The quantity of the product purchased.
     */
    private int quantity;
}
