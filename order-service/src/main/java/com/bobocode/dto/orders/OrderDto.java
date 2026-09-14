package com.bobocode.dto.orders;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Long id;

    /**
     * The date and time when the order was purchased.
     */
    @NotNull
    private LocalDateTime purchaseDate;

    /**
     * The list of items included in the order.
     */
    @NotEmpty
    private List<OrderItemDto> items;
}
