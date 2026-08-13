package com.bobocode.dto.orders;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing an order.
 */
@Data
public class OrderDto {
    /**
     * The unique identifier of the order.
     */
    private long id;

    /**
     * The date and time when the order was purchased.
     */
    private LocalDateTime purchaseDate;

    /**
     * The list of items included in the order.
     */
    private List<OrderItemDto> items;
}
