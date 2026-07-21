package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BucketServiceTest {

    private BucketService bucketService;

    @BeforeEach
    void setUp() {
        bucketService = new BucketService();
    }

    @Test
    @DisplayName("Should successfully add a product to the bucket")
    void testAddProductToBucket() {
        // Arrange
        Bucket bucket = new Bucket();
        Product product = new Product(1L, "T-shirt", BigDecimal.valueOf(20));

        // Act
        bucketService.addProductToBucket(bucket, product);

        // Assert
        assertEquals(1, bucket.getProductsInBucket().size(), "Bucket should contain 1 product");
        assertTrue(bucket.getProductsInBucket().contains(product), "Bucket should contain the added product");
    }

    @Test
    @DisplayName("Should throw NullPointerException when adding product to a null bucket")
    void testAddProductToBucket_NullBucket() {
        // Arrange
        Product product = new Product(1L, "T-shirt", BigDecimal.valueOf(20));

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> bucketService.addProductToBucket(null, product),
                "Adding a product to a null bucket should throw NullPointerException");
    }

    @Test
    @DisplayName("Should successfully remove a product from the bucket")
    void testRemoveProductFromBucket() {
        // Arrange
        Bucket bucket = new Bucket();
        Product product = new Product(1L, "T-shirt", BigDecimal.valueOf(20));
        bucket.getProductsInBucket().add(product);

        // Act
        bucketService.removeProductFromBucket(bucket, product);

        // Assert
        assertTrue(bucket.getProductsInBucket().isEmpty(), "Bucket should be empty after removing the product");
    }

    @Test
    @DisplayName("Should throw NullPointerException when removing product from a null bucket")
    void testRemoveProductFromBucket_NullBucket() {
        // Arrange
        Product product = new Product(1L, "T-shirt", BigDecimal.valueOf(20));

        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> bucketService.removeProductFromBucket(null, product),
                "Removing a product from a null bucket should throw NullPointerException");
    }

    @Test
    @DisplayName("Should successfully retrieve the list of products from the bucket")
    void testGetProductsFromBucket() {
        // Arrange
        Bucket bucket = new Bucket();
        Product product1 = new Product(1L, "T-shirt", BigDecimal.valueOf(20));
        Product product2 = new Product(2L, "Keychain", BigDecimal.valueOf(1));
        bucket.getProductsInBucket().addAll(List.of(product1, product2));

        // Act
        List<Product> products = bucketService.getProductsFromBucket(bucket);

        // Assert
        assertNotNull(products, "Returned list should not be null");
        assertEquals(2, products.size(), "Returned list should contain exactly 2 products");
        assertTrue(products.contains(product1));
        assertTrue(products.contains(product2));
    }

    @Test
    @DisplayName("Should throw NullPointerException when getting products from a null bucket")
    void testGetProductsFromBucket_NullBucket() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> bucketService.getProductsFromBucket(null),
                "Getting products from a null bucket should throw NullPointerException");
    }
}