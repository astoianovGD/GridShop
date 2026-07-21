package com.bobocode.Entities.Products;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's shopping bucket.
 */
@Data
public class Bucket {

    /**
     * List of products currently added to the bucket.
     */
    private List<Product> productsInBucket = new ArrayList<>();

}
