package com.bobocode.Menus;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.User.AuthService;
import com.bobocode.Services.User.UserService;
import com.bobocode.Utility.EmailValidator;
import com.bobocode.Utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

/**
 * Menu for handling user authentication and registration.
 */
@RequiredArgsConstructor
public final class AuthMenu {

    /**
     * Service for handling sign-in logic.
     */
    @NonNull
    private final AuthService authService;

    /**
     * Service for managing users.
     */
    @NonNull
    private final UserService userService;

    /**
     * Displays the authentication menu and processes user choice.
     *
     * @param scanner the scanner for reading user input
     * @return the successfully authenticated user, or null if exiting
     */
    public AbstractUser menu(final Scanner scanner) {
        while (true) {
            System.out.println("1) Sign In \n2) Register (as a User) "
                    + "\n0) Exit");
            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.println("Enter your email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter your password: ");
                    String password = scanner.nextLine();

                    try {
                        AbstractUser loggedInUser = authService.signIn(
                                email, password);
                        System.out.println("Login successful! Welcome, "
                                + loggedInUser.getFirstName() + "!");
                        return loggedInUser;
                    } catch (EntityNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                }
                case "2" -> {
                    userService.registerNewUser(
                            createNewUserFromConsole(scanner));
                    System.out.println("Registration successful! "
                            + "You can now Sign In.");
                }
                case "0" -> {
                    return null;
                }
                default -> System.out.println("Wrong Action!");
                // 400
            }
        }
    }

    /**
     * Helper method to create a new User object from console input.
     *
     * @param scanner the scanner for reading user input
     * @return a new User populated with input data
     */
    private User createNewUserFromConsole(final Scanner scanner) {
        System.out.println("--- User Registration ---");

        String firstName = InputValidator.getValidName(scanner, "First Name");

        String lastName = InputValidator.getValidName(scanner, "Last Name");

        String email = EmailValidator.getUniqueEmailFromConsole(
                scanner, userService);

        String password = InputValidator.getValidPassword(scanner);

        int age = InputValidator.getValidAge(scanner);

        Gender gender = InputValidator.getValidGenderFromConsole(scanner);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setGender(gender);
        user.setAge(age);

        return user;
    }
}
