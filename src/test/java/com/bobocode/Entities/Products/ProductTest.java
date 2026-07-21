package com.bobocode.Entities.Products;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    @Test
    @DisplayName("Should create product using AllArgsConstructor")
    void testAllArgsConstructor() {
        // Arrange & Act
        Product product = new Product(100L, "Test Product", BigDecimal.valueOf(50.50));

        // Assert
        assertEquals(100L, product.getId(), "Product ID should match constructor argument");
        assertEquals("Test Product", product.getName(), "Product name should match constructor argument");
        assertEquals(BigDecimal.valueOf(50.50), product.getPrice(), "Product price should match constructor argument");
    }

    @Test
    @DisplayName("Should create product using NoArgsConstructor and setters")
    void testNoArgsConstructorAndSetters() {
        // Arrange
        Product product = new Product();

        // Act
        product.setId(200L);
        product.setName("Another Product");
        product.setPrice(BigDecimal.TEN);

        // Assert
        assertEquals(200L, product.getId(), "Product ID should be updated via setter");
        assertEquals("Another Product", product.getName(), "Product name should be updated via setter");
        assertEquals(BigDecimal.TEN, product.getPrice(), "Product price should be updated via setter");
    }
}