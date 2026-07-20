package com.bobocode.Utility;

import com.bobocode.Exceptions.EmailAlreadyExistsException;
import com.bobocode.Services.User.UserService;

import java.util.Scanner;

public class EmailValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static String getValidEmailFromConsole(Scanner scanner) {
        String email;
        while (true) {
            System.out.println("Enter your Email: ");
            email = scanner.nextLine();

            if (email.matches(EMAIL_REGEX)) {
                return email;
            } else {
                System.out.println("Error: Invalid email format! Please try again. (Example: test@mail.com)");
            }
        }
    }

    public static String getUniqueEmailFromConsole(Scanner scanner, UserService userService) {
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
