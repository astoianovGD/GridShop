package com.bobocode.Services.User;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserConsoleViewServiceTest {

    private final UserConsoleViewService viewService = new UserConsoleViewService();
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void printUserProfile_shouldPrintCorrectFormattedInfo() {
        User user = new User();
        user.setId(5L);
        user.setFirstName("Alex");
        user.setLastName("Brown");
        user.setAge(30);
        user.setGender(Gender.MALE);

        viewService.printUserProfile(user);

        String output = outContent.toString();
        assertThat(output).contains("USER PROFILE");
        assertThat(output).contains("Alex Brown");
    }

    @Test
    void printUserPurchaseHistory_shouldPrintEmptyMessage_whenHistoryIsNull() {
        User user = new User();
        user.setPurchaseHistory(null);

        viewService.printUserPurchaseHistory(user);

        assertThat(outContent.toString()).contains("History is empty. No purchases yet.");
    }

    @Test
    void printUserPurchaseHistory_shouldPrintReceipts_whenHistoryIsPresent() {
        User user = new User();

        Product product1 = new Product();
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("1200.00"));

        Bucket bucket = new Bucket();
        bucket.setProductsInBucket(List.of(product1));

        user.setPurchaseHistory(List.of(bucket));

        viewService.printUserPurchaseHistory(user);

        String output = outContent.toString();
        assertThat(output).contains("Receipt #1");
        assertThat(output).contains("Laptop");
    }
}