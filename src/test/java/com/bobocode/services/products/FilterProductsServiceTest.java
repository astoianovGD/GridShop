package com.bobocode.services.products;

import com.bobocode.entities.products.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterProductsServiceTest {

    private FilterProductsService filterService;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        filterService = new FilterProductsService();
        testProducts = List.of(
                new Product(1L, "Apple", BigDecimal.valueOf(50), 1L, 10),
                new Product(2L, "Banana", BigDecimal.valueOf(100), 1L, 10),
                new Product(3L, "Avocado", BigDecimal.valueOf(150), 2L, 5),
                new Product(4L, "cherry", BigDecimal.valueOf(20), 2L, 100)
        );
    }

    @Test
    @DisplayName("Should filter products by starting letter (case insensitive)")
    void testFilterProductsByLetterBeginWith() {
        List<Product> resultA = filterService.filterProductsByLetterBeginWith('A', testProducts);
        List<Product> resultC = filterService.filterProductsByLetterBeginWith('c', testProducts);
        List<Product> resultZ = filterService.filterProductsByLetterBeginWith('Z', testProducts);

        assertEquals(2, resultA.size());
        assertTrue(resultA.stream().anyMatch(p -> p.getName().equals("Apple")));
        assertTrue(resultA.stream().anyMatch(p -> p.getName().equals("Avocado")));

        assertEquals(1, resultC.size());
        assertEquals("cherry", resultC.get(0).getName());

        assertTrue(resultZ.isEmpty());
    }

    @Test
    @DisplayName("Should filter products with price higher than or equal to given value")
    void testFilterProductsByPriceHigherThan() {
        List<Product> result = filterService.filterProductsByPriceHigherThan(BigDecimal.valueOf(100), testProducts);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Banana")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Avocado")));
    }

    @Test
    @DisplayName("Should filter products with price lower than or equal to given value")
    void testFilterProductsByPriceLowerThan() {
        List<Product> result = filterService.filterProductsByPriceLowerThan(BigDecimal.valueOf(50), testProducts);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("Apple")));
        assertTrue(result.stream().anyMatch(p -> p.getName().equals("cherry")));
    }

    @Test
    @DisplayName("Should filter products by keyword containing in name (case insensitive)")
    void testFilterProductsByName() {
        List<Product> result1 = filterService.filterProductsByName("ana", testProducts);
        List<Product> result2 = filterService.filterProductsByName("AVO", testProducts);

        assertEquals(1, result1.size());
        assertEquals("Banana", result1.get(0).getName());

        assertEquals(1, result2.size());
        assertEquals("Avocado", result2.get(0).getName());
    }

    @Test
    @DisplayName("Should return all products if keyword is null or blank")
    void testFilterProductsByName_NullOrEmptyKeyword() {
        List<Product> resultNull = filterService.filterProductsByName(null, testProducts);
        List<Product> resultEmpty = filterService.filterProductsByName("   ", testProducts);

        assertEquals(testProducts.size(), resultNull.size());
        assertEquals(testProducts.size(), resultEmpty.size());
    }
}