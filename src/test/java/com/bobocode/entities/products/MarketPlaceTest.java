package com.bobocode.entities.products;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketPlaceTest {

    @Test
    @DisplayName("Should initialize with an empty marketplace")
    void testDefaultInitialization() {
        // Arrange & Act
        MarketPlace marketPlace = new MarketPlace();
        Map<Long, Product> products = marketPlace.getMarketProducts();

        // Assert
        assertNotNull(products, "Market products map should not be null");
        assertTrue(products.isEmpty(), "Marketplace should be empty upon initialization");
    }

    @Test
    @DisplayName("Should allow adding new products to the marketplace map")
    void testAddNewProductToMarketPlace() {
        // Arrange
        MarketPlace marketPlace = new MarketPlace();
        Product newProduct = new Product(3L, "Mug", BigDecimal.valueOf(10), 2L, 50);

        // Act
        marketPlace.getMarketProducts().put(newProduct.getId(), newProduct);

        // Assert
        assertEquals(1, marketPlace.getMarketProducts().size(), "Marketplace should have 1 product now");
        assertTrue(marketPlace.getMarketProducts().containsValue(newProduct), "Marketplace should contain the new Mug product");
    }
}