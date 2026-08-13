package com.bobocode.dto.products;

import lombok.Data;

/**
 * Data Transfer Object representing a category.
 */
@Data
public class CategoryDto {
    /**
     * The unique identifier of the category.
     */
    private long id;

    /**
     * The name of the category.
     */
    private String name;
}
