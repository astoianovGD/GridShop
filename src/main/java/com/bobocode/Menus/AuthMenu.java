package com.bobocode.Menus;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.User.AuthService;
import com.bobocode.Services.User.UserService;
import com.bobocode.Utility.EmailValidator;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Scanner;

@RequiredArgsConstructor
public class AuthMenu {
    private final AuthService authService;
    private final UserService userService;

    public AuthMenu(Map<Long, AbstractUser> allUsers) {
        authService = new AuthService(allUsers);
        userService = new UserService(allUsers);
    }

    public AbstractUser menu(Scanner scanner) {
        while(true) {
            System.out.println("1) Sign In \n2) Register (as a User) \n0) Exit");
            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.println("Enter your email: ");
                    String email = scanner.nextLine();
                    System.out.println("Enter your password: ");
                    String password = scanner.nextLine();

                    try {
                        AbstractUser loggedInUser = authService.signIn(email, password);
                        System.out.println("Login successful! Welcome, " + loggedInUser.getFirstName() + "!");
                        return loggedInUser;
                    } catch (EntityNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                }
                case "2" -> {
                    userService.registerNewUser(createNewUserFromConsole(scanner));
                    System.out.println("Registration successful! You can now Sign In.");
                }
                case "0" -> System.exit(0);
                default -> System.out.println("Wrong Action!");
                //400
            }
        }
    }

    private User createNewUserFromConsole(Scanner scanner) {
        System.out.println("--- User Registration ---");

        System.out.println("Enter your First Name: ");
        String firstName = scanner.nextLine();

        System.out.println("Enter your Last Name: ");
        String lastName = scanner.nextLine();

        String email = EmailValidator.getUniqueEmailFromConsole(scanner, userService);

        System.out.println("Enter your Password: ");
        String password = scanner.nextLine();

        System.out.println("Enter your Age : ");
        int age = Integer.parseInt(scanner.nextLine());

        System.out.println("Enter your Gender (MALE, FEMALE, OTHER):");
        Gender gender;

        while (true) {
            try {
                gender = Gender.valueOf(scanner.nextLine().toUpperCase());
                break;

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid gender! Please enter exactly MALE, FEMALE, or OTHER:");
            }
        }


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
