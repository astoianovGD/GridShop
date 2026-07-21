package com.bobocode.Entities.Products;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BucketTest {

    @Test
    @DisplayName("Should initialize with an empty products list to prevent NullPointerException")
    void testDefaultInitialization() {
        Bucket bucket = new Bucket();

        // Assert
        assertNotNull(bucket.getProductsInBucket(), "Products list should not be null upon initialization");
        assertTrue(bucket.getProductsInBucket().isEmpty(), "Products list should be empty upon initialization");
    }

    @Test
    @DisplayName("Should allow adding products directly to the initialized list")
    void testAddProductToBucketDirectly() {
        // Arrange
        Bucket bucket = new Bucket();
        Product dummyProduct = new Product();

        // Act
        bucket.getProductsInBucket().add(dummyProduct);

        // Assert
        assertEquals(1, bucket.getProductsInBucket().size(), "Bucket should contain exactly 1 product");
        assertTrue(bucket.getProductsInBucket().contains(dummyProduct), "Bucket should contain the added product");
    }

    @Test
    @DisplayName("Should be able to replace the entire list using setter")
    void testSetProductsInBucket() {
        // Arrange
        Bucket bucket = new Bucket();
        List<Product> newProductList = List.of(new Product(), new Product());

        // Act
        bucket.setProductsInBucket(newProductList);

        // Assert
        assertEquals(2, bucket.getProductsInBucket().size(), "Bucket should contain exactly 2 products after setting new list");
        assertEquals(newProductList, bucket.getProductsInBucket(), "Bucket list should match the set list");
    }
}