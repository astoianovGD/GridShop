package com.bobocode.dto.products;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object representing a category.
 */
@Data
public class CategoryDto {
    /**
     * The unique identifier of the category.
     */
    @NotNull
    private Long id;

    /**
     * The name of the category.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String name;
}
