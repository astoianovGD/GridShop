package com.bobocode.Menus;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.User.AuthService;
import com.bobocode.Services.User.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthMenuTest {

    private AuthService authServiceMock;
    private UserService userServiceMock;
    private AuthMenu authMenu;

    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        authServiceMock = mock(AuthService.class);
        userServiceMock = mock(UserService.class);
        authMenu = new AuthMenu(authServiceMock, userServiceMock);

        // Intercept console output
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should throw NullPointerException if dependencies are null upon creation")
    void testNullDependencies() {
        assertThrows(NullPointerException.class, () -> new AuthMenu(null, userServiceMock));
        assertThrows(NullPointerException.class, () -> new AuthMenu(authServiceMock, null));
    }

    @Test
    @DisplayName("Should return null when option 0 (Exit) is selected")
    void testMenu_Exit() {
        // Arrange
        String simulatedInput = "0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        AbstractUser result = authMenu.menu(scanner);

        // Assert
        assertNull(result, "Exiting menu should return null");
    }

    @Test
    @DisplayName("Should handle wrong action option then exit")
    void testMenu_WrongActionThenExit() {
        // Arrange
        String simulatedInput = "99\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        AbstractUser result = authMenu.menu(scanner);

        // Assert
        assertNull(result);
        assertTrue(outContent.toString().contains("Wrong Action!"));
    }

    @Test
    @DisplayName("Should successfully sign in when credentials are valid")
    void testMenu_SignIn_Success() {
        // Arrange
        // Flow: 1 (Sign In) -> email -> password -> 0 (Exit loop if menu looped, but signIn returns right away)
        String simulatedInput = "1\ntest@mail.com\nsecret123\n";
        Scanner scanner = new Scanner(simulatedInput);

        User mockUser = new User();
        mockUser.setFirstName("Alice");

        when(authServiceMock.signIn("test@mail.com", "secret123")).thenReturn(mockUser);

        // Act
        AbstractUser result = authMenu.menu(scanner);

        // Assert
        assertNotNull(result, "Should return logged-in user");
        assertEquals(mockUser, result);
        assertTrue(outContent.toString().contains("Login successful! Welcome, Alice!"));
        verify(authServiceMock, times(1)).signIn("test@mail.com", "secret123");
    }

    @Test
    @DisplayName("Should handle EntityNotFoundException during sign in and continue loop")
    void testMenu_SignIn_NotFoundThenExit() {
        // Arrange
        // Flow: 1 (Sign In) -> wrong email -> password -> 0 (Exit)
        String simulatedInput = "1\nwrong@mail.com\npass\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        when(authServiceMock.signIn("wrong@mail.com", "pass"))
                .thenThrow(new EntityNotFoundException("User not found!"));

        // Act
        AbstractUser result = authMenu.menu(scanner);

        // Assert
        assertNull(result);
        assertTrue(outContent.toString().contains("User not found!"));
    }

    @Test
    @DisplayName("Should successfully register a new user from console with invalid gender retry")
    void testMenu_Register_SuccessWithGenderRetry() {
        // Arrange
        // Flow:
        // 2 (Register)
        // -> FirstName: Bob
        // -> LastName: Builder
        // -> Email: bob@mail.com (validated as free)
        // -> Password: password123
        // -> Age: 25
        // -> Gender attempt 1: UNKNOWN (triggers error message)
        // -> Gender attempt 2: MALE (succeeds and breaks loop)
        // -> 0 (Exit main menu)
        String simulatedInput = "2\nBob\nBuilder\nbob@mail.com\npassword123\n25\nUNKNOWN\nMALE\n0\n";
        Scanner scanner = new Scanner(simulatedInput);

        doNothing().when(userServiceMock).validateEmailIsFree("bob@mail.com");

        // Act
        AbstractUser result = authMenu.menu(scanner);

        // Assert
        assertNull(result); // Exited via '0' after registration
        String output = outContent.toString();

        assertTrue(output.contains("Invalid gender! Please enter exactly MALE, FEMALE, or OTHER:"));
        assertTrue(output.contains("Registration successful! You can now Sign In."));

        // Verify user registration was called
        verify(userServiceMock, times(1)).registerNewUser(any(User.class));
    }
}