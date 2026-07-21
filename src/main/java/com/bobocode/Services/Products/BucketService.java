package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;

import java.util.List;

/**
 * Service for managing user buckets.
 */
public final class BucketService {

    /**
     * Adds a product to the specified bucket.
     *
     * @param bucket  the bucket to add the product to
     * @param product the product to be added
     */
    public void addProductToBucket(
            final Bucket bucket, final Product product) {
        bucket.getProductsInBucket().add(product);
    }

    /**
     * Removes a product from the specified bucket.
     *
     * @param bucket  the bucket to remove the product from
     * @param product the product to be removed
     */
    public void removeProductFromBucket(
            final Bucket bucket, final Product product) {
        bucket.getProductsInBucket().remove(product);
    }

    /**
     * Retrieves the list of products from the specified bucket.
     *
     * @param bucket the bucket to retrieve products from
     * @return a list of products currently in the bucket
     */
    public List<Product> getProductsFromBucket(final Bucket bucket) {
        return bucket.getProductsInBucket();
    }

}
