package com.bobocode.Utility;

import com.bobocode.Enums.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

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
    @DisplayName("Should successfully validate a correct name on first try")
    void testGetValidName_Success() {
        Scanner scanner = new Scanner("Alex\n");
        String result = InputValidator.getValidName(scanner, "First Name");

        assertEquals("Alex", result);
    }

    @Test
    @DisplayName("Should retry on invalid name format and succeed on valid input")
    void testGetValidName_RetryOnInvalid() {
        // "alex" (wrong, lowercase), "Alex123" (wrong, digits), "John" (valid)
        Scanner scanner = new Scanner("alex\nAlex123\nJohn\n");
        String result = InputValidator.getValidName(scanner, "First Name");

        assertEquals("John", result);
        String output = outContent.toString();
        assertTrue(output.contains("Invalid First Name! Must start with a capital letter and contain only letters."));
    }

    @Test
    @DisplayName("Should successfully validate a password of 8+ characters")
    void testGetValidPassword_Success() {
        Scanner scanner = new Scanner("secret123\n");
        String result = InputValidator.getValidPassword(scanner);

        assertEquals("secret123", result);
    }

    @Test
    @DisplayName("Should retry on short password and succeed on valid one")
    void testGetValidPassword_RetryOnShort() {
        Scanner scanner = new Scanner("short\nlongPassword123\n");
        String result = InputValidator.getValidPassword(scanner);

        assertEquals("longPassword123", result);
        assertTrue(outContent.toString().contains("Password is too short! It must be at least 8 characters long."));
    }

    @Test
    @DisplayName("Should successfully validate a correct age")
    void testGetValidAge_Success() {
        Scanner scanner = new Scanner("25\n");
        int result = InputValidator.getValidAge(scanner);

        assertEquals(25, result);
    }

    @Test
    @DisplayName("Should handle non-number format and out-of-range age with retries")
    void testGetValidAge_ErrorsAndRetry() {
        // "abc" (NumberFormat), "200" (out of range > 150), "-5" (out of range < 0), "30" (valid)
        Scanner scanner = new Scanner("abc\n200\n-5\n30\n");
        int result = InputValidator.getValidAge(scanner);

        assertEquals(30, result);
        String output = outContent.toString();
        assertTrue(output.contains("Invalid format! Please enter a valid number for age."));
        assertTrue(output.contains("Age must be greater than 0 and less than 150!"));
    }

    @Test
    @DisplayName("Should successfully validate a correct Gender enum value (case-insensitive with trimming)")
    void testGetValidGender_Success() {
        Scanner scanner = new Scanner("  male \n");
        Gender result = InputValidator.getValidGenderFromConsole(scanner);

        assertEquals(Gender.MALE, result);
    }

    @Test
    @DisplayName("Should retry on invalid gender and succeed on correct one")
    void testGetValidGender_RetryOnInvalid() {
        Scanner scanner = new Scanner("UNKNOWN\nFEMALE\n");
        Gender result = InputValidator.getValidGenderFromConsole(scanner);

        assertEquals(Gender.FEMALE, result);
        assertTrue(outContent.toString().contains("Invalid gender! Please enter exactly MALE, FEMALE, or OTHER:"));
    }

    @Test
    @DisplayName("Should successfully read a valid Long ID with custom prompt")
    void testGetValidId_Success() {
        Scanner scanner = new Scanner("12345\n");
        long result = InputValidator.getValidId(scanner, "Enter ID:");

        assertEquals(12345L, result);
        assertTrue(outContent.toString().contains("Enter ID:"));
    }

    @Test
    @DisplayName("Should retry on invalid ID format and succeed on valid number")
    void testGetValidId_RetryOnInvalidFormat() {
        Scanner scanner = new Scanner("not-a-number\n999\n");
        long result = InputValidator.getValidId(scanner, "Enter ID:");

        assertEquals(999L, result);
        assertTrue(outContent.toString().contains("Invalid ID format! Please enter a valid number."));
    }

    @Test
    @DisplayName("Should successfully read a valid BigDecimal price with custom prompt")
    void testGetValidPrice_Success() {
        Scanner scanner = new Scanner("49.99\n");
        BigDecimal result = InputValidator.getValidPrice(scanner, "Enter price:");

        assertEquals(new BigDecimal("49.99"), result);
        assertTrue(outContent.toString().contains("Enter price:"));
    }

    @Test
    @DisplayName("Should retry on invalid price format and succeed on valid number")
    void testGetValidPrice_RetryOnInvalidFormat() {
        Scanner scanner = new Scanner("abc\n150.50\n");
        BigDecimal result = InputValidator.getValidPrice(scanner, "Enter price:");

        assertEquals(new BigDecimal("150.50"), result);
        assertTrue(outContent.toString().contains("Invalid price format! Please enter a valid number."));
    }
}