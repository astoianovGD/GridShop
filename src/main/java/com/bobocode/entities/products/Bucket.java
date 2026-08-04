package com.bobocode.entities.products;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping bucket.
 */
@Data
public class Bucket {
    /**
     * Unique identifier of the bucket.
     */
    private Long id;

    /**
     * List of products currently added to the bucket.
     */
    private List<Product> productsInBucket = new ArrayList<>();

}
