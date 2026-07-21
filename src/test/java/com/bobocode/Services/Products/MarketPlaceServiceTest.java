package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.MarketPlace;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MarketPlaceServiceTest {

    private MarketPlace marketPlaceMock;
    private MarketPlaceService marketPlaceService;
    private Map<Long, Product> underlyingMap;

    @BeforeEach
    void setUp() {
        // Prepare a real underlying map wrapped by a mocked MarketPlace
        underlyingMap = new HashMap<>();
        marketPlaceMock = mock(MarketPlace.class);
        when(marketPlaceMock.getMarketProducts()).thenReturn(underlyingMap);

        marketPlaceService = new MarketPlaceService(marketPlaceMock);
    }

    @Test
    @DisplayName("Should throw NullPointerException immediately if marketplace is null")
    void testNullMarketPlace() {
        // Ensure you have added @NonNull to the 'marketPlace' field in MarketPlaceService
        assertThrows(NullPointerException.class,
                () -> new MarketPlaceService(null),
                "Creating MarketPlaceService with a null marketplace should throw NullPointerException");
    }

    @Test
    @DisplayName("Should assign an ID starting from 3 and add product to marketplace")
    void testAddNewProduct() {
        // Arrange
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1200.00"));

        // Act
        marketPlaceService.addNewProduct(product);

        // Assert
        assertEquals(3L, product.getId(), "First added product should have ID 3");
        assertTrue(underlyingMap.containsKey(3L), "Marketplace map should contain the product ID");
        assertEquals(product, underlyingMap.get(3L), "The stored product should match");

        // Add a second product to test ID incrementing
        Product product2 = new Product();
        marketPlaceService.addNewProduct(product2);
        assertEquals(4L, product2.getId(), "Second added product should have ID 4");
    }

    @Test
    @DisplayName("Should successfully remove an existing product by ID")
    void testRemoveProduct_Success() {
        // Arrange
        Product product = new Product(5L, "Phone", new BigDecimal("500.00"));
        underlyingMap.put(5L, product);

        // Act
        marketPlaceService.removeProduct(5L);

        // Assert
        assertFalse(underlyingMap.containsKey(5L), "Product should be removed from the map");
        assertTrue(underlyingMap.isEmpty(), "Map should be empty");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when removing a non-existent product ID")
    void testRemoveProduct_NotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> marketPlaceService.removeProduct(99L),
                "Should throw EntityNotFoundException if product to remove does not exist");
    }

    @Test
    @DisplayName("Should successfully edit/update an existing product")
    void testEditProduct() {
        // Arrange
        Product product = new Product(3L, "Old Name", new BigDecimal("100.00"));
        underlyingMap.put(3L, product);

        Product updatedProduct = new Product(3L, "New Name", new BigDecimal("150.00"));

        // Act
        marketPlaceService.editProduct(updatedProduct);

        // Assert
        assertEquals("New Name", underlyingMap.get(3L).getName(), "Product name should be updated");
        assertEquals(new BigDecimal("150.00"), underlyingMap.get(3L).getPrice(), "Product price should be updated");
    }

    @Test
    @DisplayName("Should return a list of all products in the marketplace")
    void testGetAllProducts() {
        // Arrange
        Product p1 = new Product(3L, "Item 1", BigDecimal.TEN);
        Product p2 = new Product(4L, "Item 2", BigDecimal.ONE);
        underlyingMap.put(3L, p1);
        underlyingMap.put(4L, p2);

        // Act
        List<Product> products = marketPlaceService.getAllProducts();

        // Assert
        assertNotNull(products, "Returned list should not be null");
        assertEquals(2, products.size(), "List should contain 2 products");
        assertTrue(products.contains(p1));
        assertTrue(products.contains(p2));
    }

    @Test
    @DisplayName("Should successfully retrieve a product by its ID")
    void testGetProductById_Success() {
        // Arrange
        Product expectedProduct = new Product(10L, "Tablet", new BigDecimal("300.00"));
        underlyingMap.put(10L, expectedProduct);

        // Act
        Product actualProduct = marketPlaceService.getProductById(10L);

        // Assert
        assertNotNull(actualProduct, "Retrieved product should not be null");
        assertEquals(expectedProduct, actualProduct, "Retrieved product should match the expected one");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting a product by non-existent ID")
    void testGetProductById_NotFound() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> marketPlaceService.getProductById(999L),
                "Should throw EntityNotFoundException for a missing product ID");
    }
}