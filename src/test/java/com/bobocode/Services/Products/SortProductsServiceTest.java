package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortProductsServiceTest {

    private SortProductsService sortService;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        sortService = new SortProductsService();
        testProducts = List.of(
                new Product(1L, "Zebra", BigDecimal.valueOf(150), 1L, 1),
                new Product(2L, "Apple", BigDecimal.valueOf(50), 1L, 1),
                new Product(3L, "Mango", BigDecimal.valueOf(100), 1L, 1)
        );
    }

    @Test
    @DisplayName("Should sort products by price ascending")
    void testSortProductsByPriceAsc() {
        List<Product> result = sortService.sortProductsByPriceAsc(testProducts);

        assertEquals("Apple", result.get(0).getName()); // 50
        assertEquals("Mango", result.get(1).getName()); // 100
        assertEquals("Zebra", result.get(2).getName()); // 150
    }

    @Test
    @DisplayName("Should sort products by price descending")
    void testSortProductsByPriceDesc() {
        List<Product> result = sortService.sortProductsByPriceDesc(testProducts);

        assertEquals("Zebra", result.get(0).getName()); // 150
        assertEquals("Mango", result.get(1).getName()); // 100
        assertEquals("Apple", result.get(2).getName()); // 50
    }

    @Test
    @DisplayName("Should sort products by name ascending")
    void testSortProductsByNameAsc() {
        List<Product> result = sortService.sortProductsByNameAsc(testProducts);

        assertEquals("Apple", result.get(0).getName());
        assertEquals("Mango", result.get(1).getName());
        assertEquals("Zebra", result.get(2).getName());
    }

    @Test
    @DisplayName("Should sort products by name descending")
    void testSortProductsByNameDesc() {
        List<Product> result = sortService.sortProductsByNameDesc(testProducts);

        assertEquals("Zebra", result.get(0).getName());
        assertEquals("Mango", result.get(1).getName());
        assertEquals("Apple", result.get(2).getName());
    }
}