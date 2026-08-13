package com.bobocode.dto.bucket;

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
    private long productId;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The price of a single unit of the product.
     */
    private BigDecimal price;

    /**
     * The name of the category to which the product belongs.
     */
    private String categoryName;

    /**
     * The quantity of this product currently in the bucket.
     */
    private int quantity;
}
