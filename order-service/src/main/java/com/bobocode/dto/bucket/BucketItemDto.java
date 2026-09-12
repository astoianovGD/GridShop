package com.bobocode.dto.bucket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object representing an item within a user's shopping bucket.
 */
@Data
public class BucketItemDto {

    /**
     * The unique identifier of the product.
     */
    @NotNull(message = "Product ID must not be null")
    private Long productId;

    /**
     * The name of the product.
     */
    @NotBlank(message = "Product name must not be blank")
    @NotNull
    @Size(max = 50)
    private String name;

    /**
     * The price of a single unit of the product.
     */
    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be greater than or equal to 0")
    private BigDecimal price;

    /**
     * The name of the category to which the product belongs.
     */
    @NotBlank(message = "Category name must not be blank")
    @NotNull
    @Size(max = 50)
    private String categoryName;

    /**
     * The quantity of this product currently in the bucket.
     */
    @NotNull(message = "Quantity must not be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}