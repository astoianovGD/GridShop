package com.bobocode.Menus;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.MarketPlaceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BucketMenuTest {

    private BucketService bucketServiceMock;
    private MarketPlaceService marketPlaceServiceMock;
    private CatalogMenu catalogMenuMock;
    private BucketMenu bucketMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        bucketServiceMock = mock(BucketService.class);
        marketPlaceServiceMock = mock(MarketPlaceService.class);
        catalogMenuMock = mock(CatalogMenu.class);

        bucketMenu = new BucketMenu(bucketServiceMock, marketPlaceServiceMock, catalogMenuMock);

        // Intercept console output
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if any dependency is null upon creation")
    void testNullDependencies() {
        // Ensure you have added @NonNull to all fields in BucketMenu
        assertThrows(NullPointerException.class, () -> new BucketMenu(null, marketPlaceServiceMock, catalogMenuMock));
        assertThrows(NullPointerException.class, () -> new BucketMenu(bucketServiceMock, null, catalogMenuMock));
        assertThrows(NullPointerException.class, () -> new BucketMenu(bucketServiceMock, marketPlaceServiceMock, null));
    }

    @Test
    @DisplayName("Should display message and return immediately if bucket is empty")
    void testHandleBucket_EmptyBucket() {
        // Arrange
        User user = new User();
        user.setBucket(new Bucket()); // empty bucket

        Scanner scanner = new Scanner("1\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Your bucket is empty! Add some products first."));
        verify(catalogMenuMock, never()).catalogAllProducts(any());
    }

    @Test
    @DisplayName("Should return back when option 0 (Go Back) is selected")
    void testHandleBucket_GoBack() {
        // Arrange
        User user = new User();
        Bucket bucket = new Bucket();
        Product product = new Product(1L, "Apple", BigDecimal.TEN);
        bucket.getProductsInBucket().add(product);
        user.setBucket(bucket);

        // Flow: 0 -> Go Back
        Scanner scanner = new Scanner("0\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        verify(catalogMenuMock, times(1)).catalogAllProducts(any());
    }

    @Test
    @DisplayName("Should handle invalid option in bucket menu then exit")
    void testHandleBucket_InvalidOptionThenExit() {
        // Arrange
        User user = new User();
        Bucket bucket = new Bucket();
        bucket.getProductsInBucket().add(new Product(1L, "Apple", BigDecimal.TEN));
        user.setBucket(bucket);

        // Flow: 99 (invalid option) -> 0 (Go Back)
        Scanner scanner = new Scanner("99\n0\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        assertTrue(outContent.toString().contains("Invalid option!"));
    }

    @Test
    @DisplayName("Should successfully remove item from bucket by ID")
    void testHandleBucket_RemoveItemSuccess() {
        // Arrange
        User user = new User();
        Bucket bucket = new Bucket();
        Product product = new Product(5L, "Phone", BigDecimal.valueOf(500));
        bucket.getProductsInBucket().add(product);
        user.setBucket(bucket);

        when(marketPlaceServiceMock.getProductById(5L)).thenReturn(product);

        // Flow: 2 (Remove Item) -> 5 (Product ID) -> 0 (Go Back)
        Scanner scanner = new Scanner("2\n5\n0\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        verify(marketPlaceServiceMock, times(1)).getProductById(5L);
        verify(bucketServiceMock, times(1)).removeProductFromBucket(bucket, product);
        assertTrue(outContent.toString().contains("Product successfully removed!"));
    }

    @Test
    @DisplayName("Should handle NumberFormatException when entering text instead of ID for removal")
    void testHandleBucket_RemoveItemInvalidIdFormat() {
        // Arrange
        User user = new User();
        Bucket bucket = new Bucket();
        bucket.getProductsInBucket().add(new Product(1L, "Phone", BigDecimal.valueOf(500)));
        user.setBucket(bucket);

        // Flow: 2 (Remove Item) -> "abc" (invalid ID text) -> 0 (Go Back)
        Scanner scanner = new Scanner("2\nabc\n0\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a number."));
        verify(marketPlaceServiceMock, never()).getProductById(anyLong());
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException when product ID does not exist during removal")
    void testHandleBucket_RemoveItemNotFound() {
        // Arrange
        User user = new User();
        Bucket bucket = new Bucket();
        bucket.getProductsInBucket().add(new Product(1L, "Phone", BigDecimal.valueOf(500)));
        user.setBucket(bucket);

        when(marketPlaceServiceMock.getProductById(99L))
                .thenThrow(new EntityNotFoundException("Product with ID 99 not found!"));

        // Flow: 2 (Remove Item) -> 99 (Non-existent ID) -> 0 (Go Back)
        Scanner scanner = new Scanner("2\n99\n0\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        assertTrue(outContent.toString().contains("Product with ID 99 not found!"));
        verify(bucketServiceMock, never()).removeProductFromBucket(any(), any());
    }

    @Test
    @DisplayName("Should successfully process checkout, move bucket to history, and reset bucket")
    void testHandleBucket_CheckoutSuccess() {
        // Arrange
        User user = new User();
        Bucket initialBucket = new Bucket();
        initialBucket.getProductsInBucket().add(new Product(1L, "Laptop", BigDecimal.valueOf(1000)));
        user.setBucket(initialBucket);
        user.setPurchaseHistory(new ArrayList<>());

        when(bucketServiceMock.getProductsFromBucket(initialBucket)).thenReturn(initialBucket.getProductsInBucket());

        // Flow: 1 (Purchase Items) -> "1234-5678-9012-3456" (Card number inside checkout)
        Scanner scanner = new Scanner("1\n1234-5678-9012-3456\n");

        // Act
        bucketMenu.handleBucket(user, scanner);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Enter your card number:"));
        assertTrue(output.contains("Success!!! Purchase was made!"));

        // Verify history contains old bucket and user has a brand new bucket instance
        assertEquals(1, user.getPurchaseHistory().size());
        assertEquals(initialBucket, user.getPurchaseHistory().get(0));
        assertNotNull(user.getBucket());
        assertNotEquals(initialBucket, user.getBucket(), "User should be assigned a new bucket instance");
    }
}