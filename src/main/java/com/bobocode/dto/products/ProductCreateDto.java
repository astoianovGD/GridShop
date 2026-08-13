package com.bobocode.dto.products;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a product.
 */
@Data
public class ProductCreateDto {
    /**
     * The name of the product.
     */
    private String name;

    /**
     * The price of the product.
     */
    private BigDecimal price;

    /**
     * The unique identifier of the category the product belongs to.
     */
    private long categoryId;
}
