package com.bobocode.Entities.Products;

import lombok.Data;

/**
 * Represents a product category.
 */
@Data
public class Category {

    /**
     * Unique identifier for the category.
     */
    private long id;

    /**
     * The name of the category.
     */
    private String name;
}
