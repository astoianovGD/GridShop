package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterProductsServiceTest {

    private FilterProductsService filterService;
    private List<Product> sampleProducts;

    @BeforeEach
    void setUp() {
        filterService = new FilterProductsService();

        // Prepare test data
        Product p1 = new Product(1L, "Apple iPhone", new BigDecimal("999.99"));
        Product p2 = new Product(2L, "Banana Juice", new BigDecimal("2.50"));
        Product p3 = new Product(3L, "AirPods", new BigDecimal("150.00"));
        Product p4 = new Product(4L, "Notebook", new BigDecimal("10.00"));

        sampleProducts = List.of(p1, p2, p3, p4);
    }

    @Test
    @DisplayName("Should filter products starting with a specific letter (case-insensitive)")
    void testFilterProductsByLetterBeginWith() {
        // Act
        // Filtering products starting with 'A' or 'a' (Apple iPhone, AirPods)
        List<Product> result = filterService.filterProductsByLetterBeginWith('a', sampleProducts);

        // Assert
        assertEquals(2, result.size(), "Should find exactly 2 products starting with 'a'");
        assertTrue(result.stream().allMatch(p -> p.getName().toLowerCase().startsWith("a")));
    }

    @Test
    @DisplayName("Should return empty list if no products match the starting letter")
    void testFilterProductsByLetterBeginWith_NoMatch() {
        // Act
        List<Product> result = filterService.filterProductsByLetterBeginWith('z', sampleProducts);

        // Assert
        assertTrue(result.isEmpty(), "Result list should be empty");
    }

    @Test
    @DisplayName("Should filter products with price higher than or equal to threshold")
    void testFilterProductsByPriceHigherThan() {
        // Act
        // Threshold: 150.00 (Apple iPhone 999.99, AirPods 150.00 should match)
        List<Product> result = filterService.filterProductsByPriceHigherThan(new BigDecimal("150.00"), sampleProducts);

        // Assert
        assertEquals(2, result.size(), "Should find 2 products with price >= 150.00");
        assertTrue(result.stream().allMatch(p -> p.getPrice().compareTo(new BigDecimal("150.00")) >= 0));
    }

    @Test
    @DisplayName("Should filter products with price lower than or equal to threshold")
    void testFilterProductsByPriceLowerThan() {
        // Act
        // Threshold: 15.00 (Banana Juice 2.50, Notebook 10.00 should match)
        List<Product> result = filterService.filterProductsByPriceLowerThan(new BigDecimal("15.00"), sampleProducts);

        // Assert
        assertEquals(2, result.size(), "Should find 2 products with price <= 15.00");
        assertTrue(result.stream().allMatch(p -> p.getPrice().compareTo(new BigDecimal("15.00")) <= 0));
    }

    @Test
    @DisplayName("Should filter products containing keyword in name (case-insensitive)")
    void testFilterProductsByName_Success() {
        // Act
        // Keyword: "phone" (Matches Apple iPhone)
        List<Product> result = filterService.filterProductsByName("phone", sampleProducts);

        // Assert
        assertEquals(1, result.size(), "Should find 1 product containing 'phone'");
        assertEquals("Apple iPhone", result.get(0).getName());
    }

    @Test
    @DisplayName("Should return original list if keyword is null")
    void testFilterProductsByName_NullKeyword() {
        // Act
        List<Product> result = filterService.filterProductsByName(null, sampleProducts);

        // Assert
        assertEquals(sampleProducts.size(), result.size(), "Should return the full list if keyword is null");
    }

    @Test
    @DisplayName("Should return original list if keyword is empty or whitespace")
    void testFilterProductsByName_EmptyOrBlankKeyword() {
        // Act & Assert
        assertEquals(sampleProducts.size(), filterService.filterProductsByName("", sampleProducts).size());
        assertEquals(sampleProducts.size(), filterService.filterProductsByName("   ", sampleProducts).size());
    }
}