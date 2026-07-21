package com.bobocode.Utility;

import com.bobocode.Exceptions.EmailAlreadyExistsException;
import com.bobocode.Services.User.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailValidatorTest {

    // Streams to mock console input and intercept console output
    private final PrintStream originalOut = System.out;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Should successfully return valid email on the first try")
    void testGetValidEmailFromConsole_Success() {
        // Arrange
        // Simulate user typing an invalid email first, then a valid one
        String simulatedInput = "invalid-email\ncorrect.email@test.com\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Act
        String resultEmail = EmailValidator.getValidEmailFromConsole(scanner);

        // Assert
        assertEquals("correct.email@test.com", resultEmail, "Should return the valid email");

        // Verify that the error message for the invalid attempt was printed
        String consoleOutput = outContent.toString();
        org.junit.jupiter.api.Assertions.assertTrue(consoleOutput.contains("Error: Invalid email format!"));
    }

    @Test
    @DisplayName("Should successfully return a unique valid email when userService allows it")
    void testGetUniqueEmailFromConsole_Success() {
        // Arrange
        String simulatedInput = "unique@bobocode.com\n";
        Scanner scanner = new Scanner(simulatedInput);

        // Mock UserService to do nothing (meaning email is free)
        UserService userServiceMock = Mockito.mock(UserService.class);
        doNothing().when(userServiceMock).validateEmailIsFree("unique@bobocode.com");

        // Act
        String resultEmail = EmailValidator.getUniqueEmailFromConsole(scanner, userServiceMock);

        // Assert
        assertEquals("unique@bobocode.com", resultEmail);
        verify(userServiceMock, times(1)).validateEmailIsFree("unique@bobocode.com");
    }

    @Test
    @DisplayName("Should prompt again if email is already taken, then accept a free email")
    void testGetUniqueEmailFromConsole_AlreadyTakenThenSuccess() {
        // Arrange
        // User first enters a taken email, then a free one
        String simulatedInput = "taken@bobocode.com\nfree@bobocode.com\n";
        Scanner scanner = new Scanner(simulatedInput);

        UserService userServiceMock = Mockito.mock(UserService.class);

        // First call throws exception (taken), second call does nothing (free)
        doThrow(new EmailAlreadyExistsException("Error 409: Already taken!"))
                .when(userServiceMock).validateEmailIsFree("taken@bobocode.com");
        doNothing()
                .when(userServiceMock).validateEmailIsFree("free@bobocode.com");

        // Act
        String resultEmail = EmailValidator.getUniqueEmailFromConsole(scanner, userServiceMock);

        // Assert
        assertEquals("free@bobocode.com", resultEmail, "Should eventually return the free email");

        // Verify that the exception message was printed to the console
        String consoleOutput = outContent.toString();
        org.junit.jupiter.api.Assertions.assertTrue(consoleOutput.contains("Error 409: Already taken!"));

        // Verify userService was called twice
        verify(userServiceMock, times(1)).validateEmailIsFree("taken@bobocode.com");
        verify(userServiceMock, times(1)).validateEmailIsFree("free@bobocode.com");
    }
}