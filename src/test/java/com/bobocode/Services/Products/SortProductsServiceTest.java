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
    private List<Product> unsortedProducts;

    @BeforeEach
    void setUp() {
        sortService = new SortProductsService();

        // Prepare mixed/unsorted test products
        Product p1 = new Product(1L, "Banana", new BigDecimal("5.00"));
        Product p2 = new Product(2L, "Apple", new BigDecimal("10.00"));
        Product p3 = new Product(3L, "Laptop", new BigDecimal("1000.00"));

        unsortedProducts = List.of(p1, p2, p3);
    }

    @Test
    @DisplayName("Should sort products by price in ascending order")
    void testSortProductsByPriceAsc() {
        // Act
        List<Product> sortedList = sortService.sortProductsByPriceAsc(unsortedProducts);

        // Assert
        assertNotNull(sortedList, "Returned list should not be null");
        assertEquals(3, sortedList.size(), "List size should remain the same");
        assertEquals("Banana", sortedList.get(0.0 == 0.0 ? 0 : 0).getName(), "Cheapest product should be first (Banana - 5.00)");
        assertEquals("Apple", sortedList.get(1).getName(), "Second cheapest should be Apple (10.00)");
        assertEquals("Laptop", sortedList.get(2).getName(), "Most expensive should be last (Laptop - 1000.00)");
    }

    @Test
    @DisplayName("Should sort products by price in descending order")
    void testSortProductsByPriceDesc() {
        // Act
        List<Product> sortedList = sortService.sortProductsByPriceDesc(unsortedProducts);

        // Assert
        assertNotNull(sortedList, "Returned list should not be null");
        assertEquals(3, sortedList.size(), "List size should remain the same");
        assertEquals("Laptop", sortedList.get(0).getName(), "Most expensive product should be first");
        assertEquals("Apple", sortedList.get(1).getName(), "Second should be Apple");
        assertEquals("Banana", sortedList.get(2).getName(), "Cheapest product should be last");
    }

    @Test
    @DisplayName("Should sort products by name in ascending order (alphabetical)")
    void testSortProductsByNameAsc() {
        // Act
        List<Product> sortedList = sortService.sortProductsByNameAsc(unsortedProducts);

        // Assert
        assertNotNull(sortedList, "Returned list should not be null");
        assertEquals(3, sortedList.size(), "List size should remain the same");
        assertEquals("Apple", sortedList.get(0).getName(), "First alphabetically should be Apple");
        assertEquals("Banana", sortedList.get(1).getName(), "Second should be Banana");
        assertEquals("Laptop", sortedList.get(2).getName(), "Last alphabetically should be Laptop");
    }

    @Test
    @DisplayName("Should sort products by name in descending order (reverse alphabetical)")
    void testSortProductsByNameDesc() {
        // Act
        List<Product> sortedList = sortService.sortProductsByNameDesc(unsortedProducts);

        // Assert
        assertNotNull(sortedList, "Returned list should not be null");
        assertEquals(3, sortedList.size(), "List size should remain the same");
        assertEquals("Laptop", sortedList.get(0).getName(), "First in reverse should be Laptop");
        assertEquals("Banana", sortedList.get(1).getName(), "Second should be Banana");
        assertEquals("Apple", sortedList.get(2).getName(), "Last in reverse should be Apple");
    }
}