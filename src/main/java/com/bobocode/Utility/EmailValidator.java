package com.bobocode.Utility;

import com.bobocode.Exceptions.EmailAlreadyExistsException;
import com.bobocode.Services.User.UserService;

import java.util.Scanner;

/**
 * Utility class for validating user email addresses.
 */
public final class EmailValidator {

    /** Regular expression for validating standard email formats. */
    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private EmailValidator() {
        // Utility class
    }

    /**
     * Prompts the user to enter a valid email address via console.
     *
     * @param scanner the scanner used to read user input
     * @return a valid email string
     */
    public static String getValidEmailFromConsole(final Scanner scanner) {
        String email;
        while (true) {
            System.out.println("Enter your Email: ");
            email = scanner.nextLine();

            if (email.matches(EMAIL_REGEX)) {
                return email;
            } else {
                System.out.println("Error: Invalid email format! "
                        + "Please try again. (Example: test@mail.com)");
            }
        }
    }

    /**
     * Prompts the user to enter a valid and unique email address via console.
     *
     * @param scanner     the scanner used to read user input
     * @param userService the user service to check email uniqueness against
     * @return a valid and unique email string
     */
    public static String getUniqueEmailFromConsole(
            final Scanner scanner, final UserService userService) {
        while (true) {
            String email = getValidEmailFromConsole(scanner);

            try {

                userService.validateEmailIsFree(email);

                return email;

            } catch (EmailAlreadyExistsException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
