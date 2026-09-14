package com.bobocode.dto.products;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String name;

    /**
     * The price of the product.
     */
    @NotNull
    @Min(0)
    private BigDecimal price;

    /**
     * The unique identifier of the category the product belongs to.
     */
    @NotNull
    private Long categoryId;
}
