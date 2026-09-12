package com.bobocode.dto.products;

import jakarta.validation.constraints.*;
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
    @NotNull
    private Long id;

    /**
     * The name of the product.
     */
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String name;

    /**
     * The price of the product.
     */
    @NotNull
    @Min(0)
    private BigDecimal price;

    /**
     * The name of the category the product belongs to.
     */
    
    private String categoryName;

    /**
     * The active status of the product.
     */
    private Boolean isActive;
}
