package com.bobocode;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    @DisplayName("Should initialize first admin, exit auth menu (0), and shut down system")
    void testMain_SetupAdminAndExit() {
        // Arrange
        // Simulation flow for Main:
        // 1. Admin First Name: John
        // 2. Admin Last Name: Doe
        // 3. Admin Email: admin@test.com
        // 4. Admin Password: secretPassword
        // 5. AuthMenu option: 0 (Exit system)
        String simulatedInput = "John\nDoe\nadmin@test.com\nsecretPassword\n0\n";
        ByteArrayInputStream inContent = new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8));
        System.setIn(inContent);

        // Act
        Main.main(new String[]{});

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("--- SYSTEM SETUP: CREATE FIRST ADMIN ---"));
        assertTrue(output.contains("Admin successfully created!"));
        assertTrue(output.contains("Shutting down the system..."));
    }
}