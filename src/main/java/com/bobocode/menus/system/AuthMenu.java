package com.bobocode.menus.system;

import com.bobocode.dto.users.UserRegistrationDto;
import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.services.system.AuthService;
import com.bobocode.services.user.UserService;
import com.bobocode.utility.EmailValidator;
import com.bobocode.utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Menu for handling user authentication and registration.
 */
@RequiredArgsConstructor
@Component
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
    public User menu(final Scanner scanner) {
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
                        User loggedInUser = authService.signIn(
                                email, password);
                        System.out.println("Login successful! Welcome, "
                                + loggedInUser.getFirstname() + "!");
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
            }
        }
    }

    /**
     * Helper method to create a new User object from console input.
     *
     * @param scanner the scanner for reading user input
     * @return a new User populated with input data
     */
    private UserRegistrationDto createNewUserFromConsole(
            final Scanner scanner) {
        System.out.println("--- User Registration ---");

        String firstName = InputValidator.getValidName(scanner, "First Name");

        String lastName = InputValidator.getValidName(scanner, "Last Name");

        String email = EmailValidator.getUniqueEmailFromConsole(
                scanner, userService);

        String password = InputValidator.getValidPassword(scanner);

        int age = InputValidator.getValidAge(scanner);

        Gender gender = InputValidator.getValidGenderFromConsole(scanner);

        UserRegistrationDto user = new UserRegistrationDto();
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);
        user.setPassword(password);
        user.setGender(gender);
        user.setAge(age);

        return user;
    }
}
