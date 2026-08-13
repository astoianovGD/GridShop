package com.bobocode.dto.products;

import lombok.Data;

/**
 * Data Transfer Object for creating a category.
 */
@Data
public class CategoryCreateDto {
    /**
     * The name of the category to create.
     */
    private String name;
}
