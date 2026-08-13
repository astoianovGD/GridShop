package com.bobocode.menus.users;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.enums.Gender;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.menus.products.BucketMenu;
import com.bobocode.menus.products.CatalogMenu;
import com.bobocode.services.bucket.BucketService;
import com.bobocode.services.orders.OrderConsoleViewService;
import com.bobocode.services.orders.OrderService;
import com.bobocode.services.products.MarketPlaceService;
import com.bobocode.services.products.ProductsConsoleViewService;
import com.bobocode.services.user.UserConsoleViewService;
import com.bobocode.services.user.UserService;
import com.bobocode.utility.EmailValidator;
import com.bobocode.utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Menu for user operations.
 */
@RequiredArgsConstructor
@Component
public final class UserMenu {

    /** The user service. */
    @NonNull
    private final UserService userService;

    /** The bucket service. */
    @NonNull
    private final BucketService bucketService;

    /** The marketplace service. */
    @NonNull
    private final MarketPlaceService marketPlaceService;

    /** The catalog menu. */
    @NonNull
    private final CatalogMenu catalogMenu;

    /** The user console view service. */
    @NonNull
    private final UserConsoleViewService userConsoleViewService;

    /** The bucket menu. */
    @NonNull
    private final BucketMenu bucketMenu;

    /** The order service. */
    @NonNull
    private final OrderService orderService;

    /** The order console view service. */
    @NonNull
    private final OrderConsoleViewService orderConsoleViewService;

    /** The products console view service. */
    @NonNull
    private final ProductsConsoleViewService productsConsoleViewService;


    /**
     * Displays the user menu and handles user input.
     *
     * @param user    the current user
     * @param scanner the scanner for input
     */
    public void menu(final UserDto user, final Scanner scanner) {
        while (true) {

            System.out.println("\n--- User Menu ---");
            System.out.println("1) Browse Products \n"
                    + "2) View Personal Data \n"
                    + "3) View Bucket \n4) Delete Account \n0) Sign Out");
            switch (scanner.nextLine()) {
                case "1" -> handleBrowseProducts(user, scanner);
                case "2" -> handleViewPersonalData(user, scanner);
                case "3" -> bucketMenu.handleBucket(user, scanner);
                case "4" -> {
                    System.out.println("Are you sure? Y/N");
                    if (scanner.nextLine().equalsIgnoreCase("Y")) {
                        userService.deleteUserAccount(user.getId());
                        return;
                    }
                }
                case "0" -> {
                    System.out.println("Getting out of the system...");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Handles the browse products menu option.
     *
     * @param user    the current user
     * @param scanner the scanner for input
     */
    private void handleBrowseProducts(final UserDto user,
                                      final Scanner scanner) {
        productsConsoleViewService.catalogAllProducts(
                marketPlaceService.getAllProducts()
        );

        while (true) {
            System.out.println("Wanna do anything else?");
            System.out.println("1) Sort \n2) Filter \n3) Search \n"
                    + "4) Add to Bucket \n0) Nothing");
            String option = scanner.nextLine();
            if ("0".equals(option)) {
                break;
            }

            switch (option) {
                case "1", "2", "3" -> catalogMenu.handleOptions(
                        option, scanner);
                case "4" -> {
                    long id = InputValidator.getValidId(
                            scanner, "Enter id of product to add to Bucket:"
                    );

                    System.out.println("Enter quantity:");
                    int amount;
                    try {
                        amount = Integer.parseInt(scanner.nextLine());
                        if (amount <= 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(
                                "Invalid quantity! Must be a positive number."
                        );
                        continue;
                    }

                    try {
                        ProductDto productToAdd =
                                marketPlaceService.getProductById(id);

                        bucketService.addProductToBucket(
                                user.getId(), productToAdd.getId(), amount
                        );

                        System.out.println(
                                amount + "x " + productToAdd.getName()
                                        + " added to bucket!"
                        );
                    } catch (EntityNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Handles the view personal data menu option.
     *
     * @param user    the current user
     * @param scanner the scanner for input
     */
    private void handleViewPersonalData(
            final UserDto user, final Scanner scanner) {
        userConsoleViewService.printUserProfile(user);

        while (true) {
            System.out.println("\nWanna do anything else?");
            System.out.println("1) Edit profile \n"
                    + "2) See Purchase History \n0) Nothing");

            String option = scanner.nextLine();
            if ("0".equals(option)) {
                break;
            }
            switch (option) {
                case "1" -> editUserProfile(scanner, user);
                case "2" -> {
                    var history = orderService.getUserOrders(user.getId());
                    orderConsoleViewService.displayOrderHistory(history);
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Edits the user profile based on console input.
     *
     * @param scanner the scanner for input
     * @param user    the user whose profile is to be edited
     */
    private void editUserProfile(final Scanner scanner, final UserDto user) {
        System.out.println("\n--- Edit Profile ---");
        System.out.println("Select field to change:");
        System.out.println("1) First Name \n2) Last Name \n3) Age \n"
                + "4) Gender \n5) Email \n6) Password\n0) Cancel");

        String option = scanner.nextLine();

        switch (option) {
            case "1" -> {
                String newName = InputValidator.getValidName(
                        scanner, "First Name"
                );
                userService.updateUserField(
                        user.getId(), u -> u.setFirstname(newName)
                );
                user.setFirstname(newName);
                System.out.println("First Name updated!");
            }
            case "2" -> {
                String newLastName = InputValidator.getValidName(
                        scanner, "Last Name"
                );
                userService.updateUserField(
                        user.getId(), u -> u.setLastname(newLastName)
                );
                user.setLastname(newLastName);
                System.out.println("Last Name updated!");
            }
            case "3" -> {
                int newAge = InputValidator.getValidAge(scanner);
                userService.updateUserField(
                        user.getId(), u -> u.setAge(newAge)
                );
                user.setAge(newAge);
                System.out.println("Age updated!");
            }
            case "4" -> {
                Gender newGender = InputValidator.getValidGenderFromConsole(
                        scanner
                );
                userService.updateUserField(
                        user.getId(), u -> u.setGender(newGender)
                );
                user.setGender(newGender);
                System.out.println("Gender updated!");
            }
            case "5" -> {
                String newEmail = EmailValidator.getUniqueEmailFromConsole(
                        scanner, userService
                );
                userService.updateUserField(
                        user.getId(), u -> u.setEmail(newEmail)
                );
                user.setEmail(newEmail);
                System.out.println("Email updated!");
            }
            case "6" -> {
                String newPassword = InputValidator.getValidPassword(scanner);
                userService.updateUserField(
                        user.getId(), u -> u.setPassword(newPassword)
                );
                user.setPassword(newPassword);
                System.out.println("Password updated!");
            }
            case "0" -> System.out.println("Editing cancelled.");
            default -> System.out.println("Invalid option!");
        }
    }
}
