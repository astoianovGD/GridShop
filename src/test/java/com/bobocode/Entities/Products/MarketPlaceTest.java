package com.bobocode.Entities.Products;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarketPlaceTest {

    @Test
    @DisplayName("Should initialize with default products in the marketplace")
    void testDefaultInitialization() {
        // Arrange & Act
        MarketPlace marketPlace = new MarketPlace();
        Map<Long, Product> products = marketPlace.getMarketProducts();

        // Assert
        assertNotNull(products, "Market products map should not be null");
        assertEquals(2, products.size(), "Marketplace should contain exactly 2 default products");

        // Verify Product 1 (T-shirt)
        assertTrue(products.containsKey(1L), "Marketplace should contain product with ID 1");
        Product tshirt = products.get(1L);
        assertEquals(1L, tshirt.getId(), "T-shirt ID should be 1");
        assertEquals("T-shirt Grid Dynamics", tshirt.getName(), "T-shirt name should match");
        assertEquals(BigDecimal.valueOf(20), tshirt.getPrice(), "T-shirt price should be 20");

        // Verify Product 2 (Keychain)
        assertTrue(products.containsKey(2L), "Marketplace should contain product with ID 2");
        Product keychain = products.get(2L);
        assertEquals(2L, keychain.getId(), "Keychain ID should be 2");
        assertEquals("Grid Dynamics Keychain", keychain.getName(), "Keychain name should match");
        assertEquals(BigDecimal.valueOf(1), keychain.getPrice(), "Keychain price should be 1");
    }

    @Test
    @DisplayName("Should allow adding new products to the marketplace map")
    void testAddNewProductToMarketPlace() {
        // Arrange
        MarketPlace marketPlace = new MarketPlace();
        Product newProduct = new Product(3L, "Mug", BigDecimal.valueOf(10));

        // Act
        marketPlace.getMarketProducts().put(newProduct.getId(), newProduct);

        // Assert
        assertEquals(3, marketPlace.getMarketProducts().size(), "Marketplace should have 3 products now");
        assertTrue(marketPlace.getMarketProducts().containsValue(newProduct), "Marketplace should contain the new Mug product");
    }
}