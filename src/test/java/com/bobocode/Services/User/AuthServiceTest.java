package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @Test
    @DisplayName("Should throw NullPointerException immediately upon creation if map is null")
    void testNullMap() {
        // @NotNull annotation,
        //  (Fail-Fast)
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new AuthService(null),
                "Creating AuthService with null map should throw NullPointerException");

        assertTrue(exception.getMessage().contains("is marked non-null but is null"),
                "Exception message should indicate the null validation failure");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when password is null")
    void testNullPassword() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@email.com");
        mockUser.setPassword("password123");

        Map<Long, AbstractUser> allUsers = Map.of(1L, mockUser);
        AuthService authService = new AuthService(allUsers);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> authService.signIn("test@email.com", null),
                "Should throw EntityNotFoundException for null password");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when email is null")
    void testNullEmail() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("test@email.com");
        mockUser.setPassword("password123");

        Map<Long, AbstractUser> allUsers = Map.of(1L, mockUser);
        AuthService authService = new AuthService(allUsers);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> authService.signIn(null, "password123"),
                "Should throw EntityNotFoundException for null email");
    }

    @Test
    @DisplayName("Should successfully sign in and return user when email and password match")
    void testSuccessfulSignIn() {
        // Arrange
        User expectedUser = new User();
        expectedUser.setId(10L);
        expectedUser.setEmail("johndoe@bobocode.com");
        expectedUser.setPassword("superSecretPass123");
        expectedUser.setFirstName("John");

        Map<Long, AbstractUser> allUsers = Map.of(expectedUser.getId(), expectedUser);
        AuthService authService = new AuthService(allUsers);

        // Act
        AbstractUser actualUser = authService.signIn("johndoe@bobocode.com", "superSecretPass123");

        // Assert
        assertNotNull(actualUser, "The returned user should not be null");
        assertEquals(expectedUser, actualUser, "The returned user should be the exact same object from the map");
        assertEquals("johndoe@bobocode.com", actualUser.getEmail(), "Emails should match");
        assertEquals("John", actualUser.getFirstName(), "First names should match");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when credentials do not match")
    void testInvalidCredentials() {
        // Arrange
        User mockUser = new User();
        mockUser.setEmail("user@bobocode.com");
        mockUser.setPassword("correctPassword");

        Map<Long, AbstractUser> allUsers = Map.of(1L, mockUser);
        AuthService authService = new AuthService(allUsers);

        // Act & Assert
        // 1. correct email, incorrect password
        assertThrows(EntityNotFoundException.class,
                () -> authService.signIn("user@bobocode.com", "wrongPassword"),
                "Should throw exception for wrong password");

        // 2. incorrect email,  correct password
        assertThrows(EntityNotFoundException.class,
                () -> authService.signIn("wrong@bobocode.com", "correctPassword"),
                "Should throw exception for wrong email");

        // 3. incorrect email and incorrect password
        assertThrows(EntityNotFoundException.class,
                () -> authService.signIn("wrong@bobocode.com", "wrongPassword"),
                "Should throw exception for wrong email and wrong password");
    }
}