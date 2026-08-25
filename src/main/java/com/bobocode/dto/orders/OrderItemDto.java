package com.bobocode.dto.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull
    private Long orderItemId;

    /**
     * The unique identifier of the product.
     */
    @NotNull
    private Long productId;

    /**
     * The name of the product at the time of order.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String productName;

    /**
     * The price of the product at the time of purchase.
     */
    @NotNull
    @Min(0)
    private BigDecimal priceAtPurchase;

    /**
     * The quantity of the product purchased.
     */
    @NotNull
    @Min(1)
    private Integer quantity;
}
