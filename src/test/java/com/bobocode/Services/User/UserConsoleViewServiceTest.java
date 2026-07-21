package com.bobocode.Services.User;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserConsoleViewServiceTest {

    // Custom stream to intercept console output from System.out
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private UserConsoleViewService viewService;

    @BeforeEach
    void setUp() {
        // Replace the standard console output with our interceptor
        System.setOut(new PrintStream(outContent));
        viewService = new UserConsoleViewService();
    }

    @AfterEach
    void tearDown() {
        // Always restore the standard output after each test to avoid side effects
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if user is null when printing profile")
    void testPrintUserProfile_NullUser() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> viewService.printUserProfile(null),
                "Should throw NullPointerException if user is null");
    }

    @Test
    @DisplayName("Should correctly print user profile to console")
    void testPrintUserProfile_Success() {
        // Arrange
        User user = new User();
        user.setId(777L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(30);
        user.setGender(Gender.MALE);

        // Act
        viewService.printUserProfile(user);

        // Assert
        String consoleOutput = outContent.toString();

        // Verify that all key user data was printed to the screen
        assertTrue(consoleOutput.contains("USER PROFILE"));
        assertTrue(consoleOutput.contains("777"));
        assertTrue(consoleOutput.contains("John Doe"));
        assertTrue(consoleOutput.contains("30"));
        assertTrue(consoleOutput.contains(Gender.MALE.toString()));
    }

    @Test
    @DisplayName("Should throw NullPointerException if user is null when printing history")
    void testPrintUserPurchaseHistory_NullUser() {
        // Act & Assert
        assertThrows(NullPointerException.class,
                () -> viewService.printUserPurchaseHistory(null),
                "Should throw NullPointerException if user is null");
    }

    @Test
    @DisplayName("Should print 'History is empty' when purchase history is null")
    void testPrintUserPurchaseHistory_NullHistory() {
        // Arrange
        User user = new User();
        user.setPurchaseHistory(null); // Setting history to null explicitly

        // Act
        viewService.printUserPurchaseHistory(user);

        // Assert
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("History is empty. No purchases yet."));
    }

    @Test
    @DisplayName("Should print 'History is empty' when purchase history is empty")
    void testPrintUserPurchaseHistory_EmptyHistory() {
        // Arrange
        User user = new User();
        user.setPurchaseHistory(Collections.emptyList()); // Empty list

        // Act
        viewService.printUserPurchaseHistory(user);

        // Assert
        String consoleOutput = outContent.toString();
        assertTrue(consoleOutput.contains("History is empty. No purchases yet."));
    }

    @Test
    @DisplayName("Should successfully print user purchase history")
    void testPrintUserPurchaseHistory_Success() {
        // Arrange
        User user = new User();
        Bucket bucket = new Bucket();
        Product product = new Product(1L, "Laptop", new BigDecimal("1000.00"));
        bucket.getProductsInBucket().add(product);
        user.setPurchaseHistory(List.of(bucket));

        // Act
        viewService.printUserPurchaseHistory(user);

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Receipt #1:"), "Output should contain receipt header");
        assertTrue(output.contains("Laptop"), "Output should contain product name");
        assertTrue(output.contains("1000.00") || output.contains("1000,00"), "Output should contain product price");
        assertTrue(output.contains("Total:"), "Output should contain total amount");
    }
}