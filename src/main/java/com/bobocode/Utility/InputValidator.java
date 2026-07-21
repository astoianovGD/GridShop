package com.bobocode.Utility;

import com.bobocode.Enums.Gender;

import java.math.BigDecimal;
import java.util.Scanner;

public class InputValidator {

    public static String getValidName(Scanner scanner, String fieldName) {
        while (true) {
            System.out.println("Enter " + fieldName + " (starts with a capital letter, letters only):");
            String input = scanner.nextLine().trim();

            if (input.matches("^[A-Z][a-zA-Z]*$")) {
                return input;
            }
            System.out.println("Invalid " + fieldName + "! Must start with a capital letter and contain only letters.");
        }
    }

    public static String getValidPassword(Scanner scanner) {
        while (true) {
            System.out.println("Enter your password (at least 8 characters):");
            String password = scanner.nextLine();

            if (password != null && password.length() >= 8) {
                return password;
            }
            System.out.println("Password is too short! It must be at least 8 characters long.");
        }
    }

    public static int getValidAge(Scanner scanner) {
        while (true) {
            System.out.println("Enter your age:");
            String input = scanner.nextLine();
            try {
                int age = Integer.parseInt(input);
                if (age > 0 && age < 150) {
                    return age;
                }
                System.out.println("Age must be greater than 0 and less than 150!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid format! Please enter a valid number for age.");
            }
        }
    }

    /**
     * Prompts the user in a loop until a valid Gender is entered.
     *
     * @param scanner the scanner for reading user input
     * @return the valid Gender chosen by the user
     */
    public static Gender getValidGenderFromConsole(final Scanner scanner) {
        System.out.println("Enter your Gender (MALE, FEMALE, OTHER):");
        while (true) {
            try {
                return Gender.valueOf(scanner.nextLine().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid gender! Please enter exactly MALE, FEMALE, or OTHER:");
            }
        }
    }

    /**
     * Safely reads and validates a Long ID from the console with a custom prompt.
     *
     * @param scanner the scanner for reading user input
     * @param prompt  the message to display to the user
     * @return a valid long ID
     */
    public static long getValidId(final Scanner scanner, final String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID format! Please enter a valid number.");
            }
        }
    }

    public static BigDecimal getValidPrice(final Scanner scanner, final String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine().trim();
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format! Please enter a valid number.");
            }
        }
    }

}
