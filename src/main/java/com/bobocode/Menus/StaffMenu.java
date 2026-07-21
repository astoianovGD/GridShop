package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Menu for staff members to manage products and view users.
 */
@RequiredArgsConstructor
public final class StaffMenu {

    /** The user service. */
    private final UserService userService;

    /** The marketplace service. */
    private final MarketPlaceService marketPlaceService;

    /** The catalog menu. */
    private final CatalogMenu catalogMenu;

    /** The user console view service. */
    private final UserConsoleViewService userConsoleViewService;

    /**
     * Displays the staff menu and handles user input.
     *
     * @param scanner the scanner used to read user input
     */
    public void menu(final Scanner scanner) {
        while (true) {
            System.out.println("\n--- Staff Menu ---");
            System.out.println("1) Add Product \n2) Browse Products \n"
                    + "3) View User Profile \n0) Sign out");
            switch (scanner.nextLine()) {
                case "1" -> handleAddProduct(scanner);
                case "2" -> handleBrowseProducts(scanner);
                case "3" -> handleViewUsers(scanner);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Handles the logic for adding a new product.
     *
     * @param scanner the scanner used to read user input
     */
    private void handleAddProduct(final Scanner scanner) {
        Product newProduct = new Product();
        System.out.println("Enter product name: ");
        newProduct.setName(scanner.nextLine());
        System.out.println("Enter product price: ");
        try {
            newProduct.setPrice(new BigDecimal(scanner.nextLine()));
            marketPlaceService.addNewProduct(newProduct);
            System.out.println("Product successfully added!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid price format! Product not added.");
        }
    }

    /**
     * Handles browsing, removing, and editing products.
     *
     * @param scanner the scanner used to read user input
     */
    private void handleBrowseProducts(final Scanner scanner) {
        catalogMenu.catalogAllProducts(marketPlaceService.getAllProducts());

        while (true) {
            System.out.println("\nWanna do smth else?");
            System.out.println("1) Sort \n2) Filter \n3) Search \n"
                    + "4) Remove \n5) Edit \n0) Nothing");
            String option = scanner.nextLine();

            if ("0".equals(option)) {
                break;
            }

            switch (option) {
                case "1", "2", "3" -> catalogMenu.handleOptions(
                        option, scanner);
                case "4" -> handleRemoveProduct(scanner);
                case "5" -> handleEditProduct(scanner);
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Handles the removal of a product.
     *
     * @param scanner the scanner used to read user input
     */
    private void handleRemoveProduct(final Scanner scanner) {
        System.out.println("Enter id of product to delete: ");
        try {
            long id = Long.parseLong(scanner.nextLine());
            marketPlaceService.removeProduct(id);
            System.out.println("Product successfully deleted!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format! "
                    + "Please enter a valid number.");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the editing of a product.
     *
     * @param scanner the scanner used to read user input
     */
    private void handleEditProduct(final Scanner scanner) {
        System.out.println("Enter id of product to edit: ");
        try {
            long id = Long.parseLong(scanner.nextLine());
            Product product = marketPlaceService.getProductById(id);
            editProductInfoMenu(scanner, product);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format! "
                    + "Please enter a valid number.");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles viewing user profiles and their details.
     *
     * @param scanner the scanner used to read user input
     */
    private void handleViewUsers(final Scanner scanner) {
        var users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered yet.");
            return;
        }

        System.out.println("All users:");
        users.forEach(System.out::println);

        System.out.println("Enter user's id to view: ");
        try {
            long id = Long.parseLong(scanner.nextLine());
            User user = userService.getUserById(id);
            handleUserSubMenu(scanner, user);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format! "
                    + "Please enter a valid number.");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the sub-menu for viewing specific user details.
     *
     * @param scanner the scanner used to read user input
     * @param user    the user being viewed
     */
    private void handleUserSubMenu(final Scanner scanner, final User user) {
        while (true) {
            userConsoleViewService.printUserProfile(user);

            System.out.println("\nWanna view more?");
            System.out.println("1) View user's cart \n"
                    + "2) View user's purchase history \n"
                    + "0) Return to menu");
            String option = scanner.nextLine();

            if ("0".equals(option)) {
                break;
            }

            switch (option) {
                case "1" -> {
                    var cart = user.getBucket().getProductsInBucket();
                    if (cart.isEmpty()) {
                        System.out.println("Cart is empty.");
                    } else {
                        catalogMenu.catalogAllProducts(cart);
                    }
                }
                case "2" -> {
                    if (user.getPurchaseHistory().isEmpty()) {
                        System.out.println("History is empty.");
                    } else {
                        userConsoleViewService.printUserPurchaseHistory(user);
                    }
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    /**
     * Menu to edit product information.
     *
     * @param scanner the scanner used to read user input
     * @param product the product to be edited
     */
    private void editProductInfoMenu(
            final Scanner scanner, final Product product) {
        System.out.println("Edit \n1) Name \n2) Price");
        String option = scanner.nextLine();
        if (option.equals("1")) {
            System.out.println("Enter new name: ");
            product.setName(scanner.nextLine());
        } else if (option.equals("2")) {
            System.out.println("Enter new price: ");
            try {
                product.setPrice(new BigDecimal(scanner.nextLine()));
                System.out.println("Price successfully updated!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format! "
                        + "Please enter a valid number.");
            }
        } else {
            System.out.println("Invalid Option!");
        }
    }
}
