package com.bobocode.dto.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for creating a category.
 */
@Data
public class CategoryCreateDto {
    /**
     * The name of the category to create.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String name;
}
