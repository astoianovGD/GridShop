package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BucketMenuTest {

    private BucketService bucketServiceMock;
    private MarketPlaceService marketPlaceServiceMock;
    private CatalogMenu catalogMenuMock;
    private OrderService orderServiceMock;
    private BucketMenu bucketMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        bucketServiceMock = mock(BucketService.class);
        marketPlaceServiceMock = mock(MarketPlaceService.class);
        catalogMenuMock = mock(CatalogMenu.class);
        orderServiceMock = mock(OrderService.class);

        bucketMenu = new BucketMenu(bucketServiceMock, marketPlaceServiceMock, catalogMenuMock, orderServiceMock);

        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if any dependency is null upon creation")
    void testNullDependencies() {
        assertThrows(NullPointerException.class, () -> new BucketMenu(null, marketPlaceServiceMock, catalogMenuMock, orderServiceMock));
        assertThrows(NullPointerException.class, () -> new BucketMenu(bucketServiceMock, null, catalogMenuMock, orderServiceMock));
        assertThrows(NullPointerException.class, () -> new BucketMenu(bucketServiceMock, marketPlaceServiceMock, null, orderServiceMock));
        assertThrows(NullPointerException.class, () -> new BucketMenu(bucketServiceMock, marketPlaceServiceMock, catalogMenuMock, null));
    }

    @Test
    @DisplayName("Should display message and return immediately if bucket is empty")
    void testHandleBucket_EmptyBucket() {
        User user = new User();
        user.setId(1L);
        when(bucketServiceMock.getProductsFromBucket(1L)).thenReturn(Collections.emptyList());

        Scanner scanner = new Scanner("1\n");
        bucketMenu.handleBucket(user, scanner);

        assertTrue(outContent.toString().contains("Your bucket is empty! Add some products first."));
        verify(catalogMenuMock, never()).catalogAllProducts(any());
    }

    @Test
    @DisplayName("Should return back when option 0 (Go Back) is selected")
    void testHandleBucket_GoBack() {
        User user = new User();
        user.setId(1L);
        Product product = new Product(1L, "Apple", BigDecimal.TEN, 1L, 1);
        when(bucketServiceMock.getProductsFromBucket(1L)).thenReturn(List.of(product));

        Scanner scanner = new Scanner("0\n");
        bucketMenu.handleBucket(user, scanner);

        verify(catalogMenuMock, times(1)).catalogAllProducts(any());
    }

    @Test
    @DisplayName("Should handle invalid option in bucket menu then exit")
    void testHandleBucket_InvalidOptionThenExit() {
        User user = new User();
        user.setId(1L);
        when(bucketServiceMock.getProductsFromBucket(1L)).thenReturn(List.of(new Product(1L, "Apple", BigDecimal.TEN, 1L, 1)));

        Scanner scanner = new Scanner("99\n0\n");
        bucketMenu.handleBucket(user, scanner);

        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully remove item from bucket by ID")
    void testHandleBucket_RemoveItemSuccess() {
        User user = new User();
        user.setId(1L);
        Product product = new Product(5L, "Phone", BigDecimal.valueOf(500), 1L, 1);

        when(bucketServiceMock.getProductsFromBucket(1L)).thenReturn(List.of(product));

        // Flow: 2 (Remove Item) -> 5 (Product ID) -> 0 (Go Back)
        Scanner scanner = new Scanner("2\n5\n0\n");
        bucketMenu.handleBucket(user, scanner);

        verify(bucketServiceMock, times(1)).removeProductFromBucket(1L, 5L);
        assertTrue(outContent.toString().contains("Product successfully removed!"));
    }

    @Test
    @DisplayName("Should successfully process checkout and create order")
    void testHandleBucket_CheckoutSuccess() {
        User user = new User();
        user.setId(1L);

        when(bucketServiceMock.getProductsFromBucket(1L)).thenReturn(List.of(new Product(1L, "Laptop", BigDecimal.valueOf(1000), 1L, 1)));

        // Flow: 1 (Purchase Items) -> "1234567890123456" (valid card)
        Scanner scanner = new Scanner("1\n1234567890123456\n");
        bucketMenu.handleBucket(user, scanner);

        verify(orderServiceMock, times(1)).createOrderFromBucket(1L);
        assertTrue(outContent.toString().contains("Success!!! Purchase was made and saved to your history!"));
    }
}