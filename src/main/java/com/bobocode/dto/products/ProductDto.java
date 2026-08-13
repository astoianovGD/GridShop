package com.bobocode.dto.products;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object representing a product.
 */
@Data
public class ProductDto {
    /**
     * The unique identifier of the product.
     */
    private long id;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The price of the product.
     */
    private BigDecimal price;

    /**
     * The name of the category the product belongs to.
     */
    private String categoryName;

    /**
     * The active status of the product.
     */
    private boolean isActive;
}
