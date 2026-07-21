package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EmailAlreadyExistsException;
import com.bobocode.Exceptions.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    @DisplayName("Should throw NullPointerException if initialized with a null map")
    void testNullMap() {
        assertThrows(NullPointerException.class,
                () -> new UserService(null),
                "Creating UserService with a null map should throw NullPointerException");
    }

    @Test
    @DisplayName("Should assign an ID and add new user to the map")
    void testRegisterNewUser() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        UserService userService = new UserService(allUsers);
        User newUser = new User();
        newUser.setEmail("test@email.com");

        // Act
        userService.registerNewUser(newUser);

        // Assert
        assertNotNull(newUser.getId(), "Registered user should be assigned an ID");
        assertTrue(allUsers.containsKey(newUser.getId()), "The map should contain the new user's ID");
        assertEquals(newUser, allUsers.get(newUser.getId()), "The object in the map should match the registered user");
    }

    @Test
    @DisplayName("Should successfully delete a user account")
    void testDeleteUserAccount_Success() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        User user = new User();
        user.setId(5L);
        allUsers.put(5L, user);

        UserService userService = new UserService(allUsers);

        // Act
        userService.deleteUserAccount(5L);

        // Assert
        assertFalse(allUsers.containsKey(5L), "Map should no longer contain the user ID");
        assertTrue(allUsers.isEmpty(), "Map should be empty after deletion");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting non-existent ID")
    void testDeleteUserAccount_NotFound() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        UserService userService = new UserService(allUsers);

        // Act & Assert
        // In your code, if ID is not found, get() returns null.
        // (null instanceof User) is false, so it triggers IllegalArgumentException.
        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUserAccount(99L),
                "Should throw IllegalArgumentException for non-existent user");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting an ID that is not a User")
    void testDeleteUserAccount_WrongType() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        // Create an anonymous AbstractUser that is NOT a User
        AbstractUser notAUser = new AbstractUser() {};
        notAUser.setId(10L);
        allUsers.put(10L, notAUser);

        UserService userService = new UserService(allUsers);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.deleteUserAccount(10L),
                "Should throw IllegalArgumentException if the entity is not a User");
    }

    @Test
    @DisplayName("Should update user information in the map")
    void testEditPersonalInformation() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        User originalUser = new User();
        originalUser.setId(1L);
        originalUser.setFirstName("Old Name");
        allUsers.put(1L, originalUser);

        UserService userService = new UserService(allUsers);

        User changedUser = new User();
        changedUser.setId(1L);
        changedUser.setFirstName("New Name");

        // Act
        userService.editPersonalInformation(1L, changedUser);

        // Assert
        assertEquals("New Name", allUsers.get(1L).getFirstName(), "User's first name should be updated");
        assertEquals(changedUser, allUsers.get(1L), "The map should hold the updated user object");
    }

    @Test
    @DisplayName("Should return a list containing only instances of User")
    void testGetAllUsers() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        User user1 = new User();
        User user2 = new User();
        AbstractUser notAUser = new AbstractUser() {}; // Not a standard User

        allUsers.put(1L, user1);
        allUsers.put(2L, user2);
        allUsers.put(3L, notAUser); // This should be filtered out

        UserService userService = new UserService(allUsers);

        // Act
        List<User> userList = userService.getAllUsers();

        // Assert
        assertEquals(2, userList.size(), "List should contain exactly 2 User instances");
        assertTrue(userList.contains(user1), "List should contain user1");
        assertTrue(userList.contains(user2), "List should contain user2");
    }

    @Test
    @DisplayName("Should return the specific user by their ID")
    void testGetUserById_Success() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        User expectedUser = new User();
        expectedUser.setId(7L);
        allUsers.put(7L, expectedUser);

        UserService userService = new UserService(allUsers);

        // Act
        User actualUser = userService.getUserById(7L);

        // Assert
        assertNotNull(actualUser, "Returned user should not be null");
        assertEquals(expectedUser, actualUser, "Returned user should match the one in the map");
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if user ID is not found")
    void testGetUserById_NotFound() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        UserService userService = new UserService(allUsers);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(99L),
                "Should throw EntityNotFoundException for missing ID");
    }

    @Test
    @DisplayName("Should return true if email is already taken")
    void testIsEmailTaken_True() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        User existingUser = new User();
        existingUser.setEmail("taken@email.com");
        allUsers.put(1L, existingUser);

        UserService userService = new UserService(allUsers);

        // Act & Assert
        assertTrue(userService.isEmailTaken("taken@email.com"), "Should return true for an existing email");
    }

    @Test
    @DisplayName("Should return false if email is not taken")
    void testIsEmailTaken_False() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        UserService userService = new UserService(allUsers);

        // Act & Assert
        assertFalse(userService.isEmailTaken("free@email.com"), "Should return false for a non-existing email");
    }

    @Test
    @DisplayName("Should not throw an exception when validating a free email")
    void testValidateEmailIsFree_Success() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        UserService userService = new UserService(allUsers);

        // Act & Assert
        // Does not throw any exception
        assertDoesNotThrow(() -> userService.validateEmailIsFree("free@email.com"),
                "Should not throw an exception for a free email");
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when validating a taken email")
    void testValidateEmailIsFree_Taken() {
        // Arrange
        Map<Long, AbstractUser> allUsers = new HashMap<>();
        User existingUser = new User();
        existingUser.setEmail("taken@email.com");
        allUsers.put(1L, existingUser);

        UserService userService = new UserService(allUsers);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.validateEmailIsFree("taken@email.com"),
                "Should throw EmailAlreadyExistsException for an existing email");
    }
}