package com.bobocode.Entities.Products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a product in the marketplace.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * Unique identifier for the product.
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
}
