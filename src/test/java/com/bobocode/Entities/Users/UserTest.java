package com.bobocode.Entities.Users;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    @DisplayName("Should initialize User with an empty Bucket and empty Purchase History")
    void testDefaultInitialization() {
        // Arrange & Act
        User user = new User();

        // Assert
        assertNotNull(user.getBucket(), "User's bucket should not be null upon initialization");
        assertNotNull(user.getPurchaseHistory(), "User's purchase history should not be null upon initialization");

        // Ensure they are empty but ready to use
        assertTrue(user.getBucket().getProductsInBucket().isEmpty(), "Bucket should be empty");
        assertTrue(user.getPurchaseHistory().isEmpty(), "Purchase history should be empty");
    }

    @Test
    @DisplayName("toString() should be formatted correctly with User specific fields")
    void testUserToString() {
        // Arrange
        User user = new User();
        user.setId(5L);
        user.setFirstName("Alice");
        user.setLastName("Wonderland");
        user.setEmail("alice@bobocode.com");
        user.setAge(28);
        user.setGender(Gender.FEMALE);

        // Act
        String result = user.toString();

        // Assert
        String expected = "[User] ID: 5 | Name: Alice Wonderland | Email: alice@bobocode.com | Age: 28 | Gender: FEMALE";
        assertEquals(expected, result, "User's toString format does not match the expected layout");
    }

    @Test
    @DisplayName("Should be able to add buckets to purchase history")
    void testPurchaseHistoryModification() {
        // Arrange
        User user = new User();
        Bucket completedBucket = new Bucket();

        // Act
        user.getPurchaseHistory().add(completedBucket);

        // Assert
        assertEquals(1, user.getPurchaseHistory().size(), "Purchase history should contain 1 bucket");
        assertTrue(user.getPurchaseHistory().contains(completedBucket), "Purchase history should contain the added bucket");
    }
}