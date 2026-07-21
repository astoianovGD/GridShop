package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.MarketPlace;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.User.UserService;
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
    private UserService userServiceMock;
    private BucketService bucketServiceMock;
    private MarketPlaceService marketPlaceService;
    private Map<Long, Product> underlyingMap;

    @BeforeEach
    void setUp() {
        underlyingMap = new HashMap<>();
        marketPlaceMock = mock(MarketPlace.class);
        userServiceMock = mock(UserService.class);
        bucketServiceMock = mock(BucketService.class);

        when(marketPlaceMock.getMarketProducts()).thenReturn(underlyingMap);

        marketPlaceService = new MarketPlaceService(
                marketPlaceMock, userServiceMock, bucketServiceMock
        );
    }

    @Test
    @DisplayName("Should throw NullPointerException immediately if dependencies are null")
    void testNullDependencies() {
        assertThrows(NullPointerException.class,
                () -> new MarketPlaceService(null, userServiceMock, bucketServiceMock));
        assertThrows(NullPointerException.class,
                () -> new MarketPlaceService(marketPlaceMock, null, bucketServiceMock));
        assertThrows(NullPointerException.class,
                () -> new MarketPlaceService(marketPlaceMock, userServiceMock, null));
    }

    @Test
    @DisplayName("Should assign an ID starting from 3 and add product to marketplace")
    void testAddNewProduct() {
        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1200.00"));

        marketPlaceService.addNewProduct(product);

        assertEquals(3L, product.getId());
        assertTrue(underlyingMap.containsKey(3L));
        assertEquals(product, underlyingMap.get(3L));
    }

    @Test
    @DisplayName("Should successfully remove an existing product and clear it from user buckets")
    void testRemoveProduct_SuccessAndClearBuckets() {
        // Arrange
        Product product = new Product(5L, "Phone", new BigDecimal("500.00"));
        underlyingMap.put(5L, product);

        User user = new User();
        Bucket bucket = new Bucket();
        bucket.getProductsInBucket().add(product);
        user.setBucket(bucket);

        when(userServiceMock.getAllUsers()).thenReturn(List.of(user));

        // Act
        marketPlaceService.removeProduct(5L);

        // Assert
        assertFalse(underlyingMap.containsKey(5L), "Product should be removed from marketplace");
        verify(bucketServiceMock, times(1)).removeProductFromBucket(bucket, product);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when removing a non-existent product ID")
    void testRemoveProduct_NotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> marketPlaceService.removeProduct(99L));
    }

    @Test
    @DisplayName("Should successfully edit/update an existing product")
    void testEditProduct() {
        Product product = new Product(3L, "Old Name", new BigDecimal("100.00"));
        underlyingMap.put(3L, product);

        Product updatedProduct = new Product(3L, "New Name", new BigDecimal("150.00"));
        marketPlaceService.editProduct(updatedProduct);

        assertEquals("New Name", underlyingMap.get(3L).getName());
        assertEquals(new BigDecimal("150.00"), underlyingMap.get(3L).getPrice());
    }

    @Test
    @DisplayName("Should return a list of all products in the marketplace")
    void testGetAllProducts() {
        Product p1 = new Product(3L, "Item 1", BigDecimal.TEN);
        Product p2 = new Product(4L, "Item 2", BigDecimal.ONE);
        underlyingMap.put(3L, p1);
        underlyingMap.put(4L, p2);

        List<Product> products = marketPlaceService.getAllProducts();

        assertNotNull(products);
        assertEquals(2, products.size());
        assertTrue(products.contains(p1));
        assertTrue(products.contains(p2));
    }

    @Test
    @DisplayName("Should successfully retrieve a product by its ID")
    void testGetProductById_Success() {
        Product expectedProduct = new Product(10L, "Tablet", new BigDecimal("300.00"));
        underlyingMap.put(10L, expectedProduct);

        Product actualProduct = marketPlaceService.getProductById(10L);

        assertNotNull(actualProduct);
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when getting a product by non-existent ID")
    void testGetProductById_NotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> marketPlaceService.getProductById(999L));
    }
}