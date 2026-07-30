package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.CategoryService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.OrderService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;
import com.bobocode.Utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Menu for staff members to manage products and view users.
 */
@RequiredArgsConstructor
public final class StaffMenu {

    /** The user service. */
    @NonNull
    private final UserService userService;

    /** The marketplace service. */
    @NonNull
    private final MarketPlaceService marketPlaceService;

    /** The catalog menu. */
    @NonNull
    private final CatalogMenu catalogMenu;

    /** The user console view service. */
    @NonNull
    private final UserConsoleViewService userConsoleViewService;

    /** The bucket service. */
    @NonNull
    private final BucketService bucketService;

    /** The order service. */
    @NonNull
    private final OrderService orderService;

    /** The category service. */
    @NonNull
    private final CategoryService categoryService;

    /**
     * Displays the staff menu and handles user input.
     *
     * @param scanner the scanner used to read user input
     */
    public void menu(final Scanner scanner) {
        while (true) {
            System.out.println("\n--- Staff Menu ---");
            System.out.println("1) Add Product \n2) Browse Products \n"
                    + "3) View User Profile \n4) Manage Categories \n"
                    + "0) Sign out");
            switch (scanner.nextLine()) {
                case "1" -> handleAddProduct(scanner);
                case "2" -> handleBrowseProducts(scanner);
                case "3" -> handleViewUsers(scanner);
                case "4" -> handleManageCategories(scanner);
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
        var categories = marketPlaceService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println(
                    "Error: No categories found in the system! "
                            + "Please add categories to database first."
            );
            return;
        }

        System.out.println("\n--- Available Categories ---");
        categories.forEach(c -> System.out.println(
                "ID: " + c.getId() + " | Name: " + c.getName()
        ));
        System.out.println("\n");

        Product newProduct = new Product();

        long categoryId;
        while (true) {
            categoryId = InputValidator.getValidId(
                    scanner, "Enter category ID for the product:"
            );
            if (marketPlaceService.isCategoryExists(categoryId)) {
                break;
            }
            System.out.println(
                    "Category with ID " + categoryId
                            + " does not exist! Please try again."
            );
        }

        newProduct.setCategoryId(categoryId);
        System.out.println("Enter product name: ");
        newProduct.setName(scanner.nextLine());

        BigDecimal price = InputValidator.getValidPrice(
                scanner, "Enter product price:"
        );
        newProduct.setPrice(price);

        marketPlaceService.addNewProduct(newProduct);
        System.out.println("Product successfully added!");
    }

    /**
     * Handles browsing, removing, and editing products.
     *
     * @param scanner the scanner used to read user input
     */
    private void handleBrowseProducts(final Scanner scanner) {
        catalogMenu.catalogAllProducts(marketPlaceService.getAllProducts());

        if (marketPlaceService.getAllProducts().isEmpty()) {
            return;
        }

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
        long id = InputValidator.getValidId(
                scanner, "Enter id of product to delete:"
        );
        try {
            marketPlaceService.removeProduct(id);

            System.out.println("Product successfully deleted!");
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
        long id = InputValidator.getValidId(
                scanner, "Enter id of product to edit:"
        );
        try {
            Product product = marketPlaceService.getProductById(id);
            editProductInfoMenu(scanner, product);
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

        long id = InputValidator.getValidId(
                scanner, "Enter user's id to view:"
        );
        try {
            User user = userService.getUserById(id);
            handleUserSubMenu(scanner, user);
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
                    var cart = bucketService.getProductsFromBucket(
                            user.getId()
                    );
                    if (cart.isEmpty()) {
                        System.out.println("Cart is empty.");
                    } else {
                        catalogMenu.catalogAllProducts(cart);
                    }
                }
                case "2" -> {
                    var history = orderService.getOrderHistory(user.getId());
                    if (history.isEmpty()) {
                        System.out.println("History is empty.");
                    } else {
                        System.out.println(
                                "\n--- Purchase History for "
                                        + user.getFirstName() + " ---"
                        );
                        history.forEach(System.out::println);
                    }
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void handleManageCategories(final Scanner scanner) {
        while (true) {
            System.out.println("\n--- Manage Categories ---");
            System.out.println(
                    "1) Add new Category \n2) View Categories \n0) Go Back"
            );
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> {
                    String name = InputValidator.getValidName(
                            scanner, "Category"
                    );

                    try {
                        categoryService.addNewCategory(name);
                        System.out.println("Category successfully added!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("\n[ERROR] " + e.getMessage());
                    }
                }
                case "2" -> handleViewCategories(scanner);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void handleViewCategories(final Scanner scanner) {
        while (true) {
            var categories = marketPlaceService.getAllCategories();
            if (categories.isEmpty()) {
                System.out.println(
                        "\nNo categories found in the system. Add some first!"
                );
                return;
            }

            System.out.println("\n--- Available Categories ---");
            categories.forEach(c -> System.out.println(
                    "ID: " + c.getId() + " | Name: " + c.getName()
            ));
            System.out.println("----------------------------");

            System.out.println(
                    "1) Change Category \n2) Remove Category \n0) Go Back"
            );
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> {
                    long id = InputValidator.getValidId(
                            scanner, "Enter category ID to change:"
                    );

                    String newName = InputValidator.getValidName(
                            scanner, "Category"
                    );

                    try {
                        categoryService.editCategory(newName, id);
                        System.out.println("Category successfully updated!");
                    } catch (IllegalArgumentException e) {
                        System.out.println("\n[ERROR] " + e.getMessage());
                    }
                }
                case "2" -> {
                    long id = InputValidator.getValidId(
                            scanner, "Enter category ID to remove:"
                    );

                    try {
                        categoryService.removeCategory(id);
                        System.out.println("Category successfully deleted!");
                    } catch (IllegalStateException e) {
                        System.out.println("\n[ERROR] " + e.getMessage());
                    } catch (RuntimeException e) {
                        System.out.println(
                                "\n[ERROR] Unexpected error: " + e.getMessage()
                        );
                    }
                }
                case "0" -> {
                    return;
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
        System.out.println("Edit \n1) Name \n2) Price \n3) Category ID");
        String option = scanner.nextLine();

        switch (option) {
            case "1" -> {
                System.out.println("Enter new name: ");
                product.setName(scanner.nextLine());
                marketPlaceService.editProduct(product);
                System.out.println("Name successfully updated!");
            }
            case "2" -> {
                BigDecimal newPrice = InputValidator.getValidPrice(
                        scanner, "Enter new price:"
                );
                product.setPrice(newPrice);
                marketPlaceService.editProduct(product);
                System.out.println("Price successfully updated!");
            }
            case "3" -> {
                var categories = marketPlaceService.getAllCategories();
                System.out.println("\n--- Available Categories ---");
                categories.forEach(c -> System.out.println(
                        "ID: " + c.getId() + " | Name: " + c.getName()
                ));

                long newCategoryId;
                while (true) {
                    newCategoryId = InputValidator.getValidId(
                            scanner, "Enter new category ID:"
                    );
                    if (marketPlaceService.isCategoryExists(newCategoryId)) {
                        break;
                    }
                    System.out.println(
                            "Category with ID " + newCategoryId
                                    + " does not exist! Please try again."
                    );
                }

                product.setCategoryId(newCategoryId);
                marketPlaceService.editProduct(product);
                System.out.println("Category successfully updated!");
            }
            default -> System.out.println("Invalid Option!");
        }
    }
}
