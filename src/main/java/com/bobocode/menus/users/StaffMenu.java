package com.bobocode.menus.users;

import com.bobocode.dto.products.CategoryCreateDto;
import com.bobocode.dto.products.CategoryDto;
import com.bobocode.dto.products.ProductCreateDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.entities.products.Category;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.menus.products.CatalogMenu;
import com.bobocode.services.bucket.BucketConsoleViewService;
import com.bobocode.services.bucket.BucketService;
import com.bobocode.services.orders.OrderConsoleViewService;
import com.bobocode.services.orders.OrderService;
import com.bobocode.services.products.CategoryService;
import com.bobocode.services.products.MarketPlaceService;
import com.bobocode.services.products.ProductsConsoleViewService;
import com.bobocode.services.user.UserConsoleViewService;
import com.bobocode.services.user.UserService;
import com.bobocode.utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

/**
 * Menu for staff members to manage products and view users.
 */
@RequiredArgsConstructor
@Component
public final class StaffMenu {

    /**
     * Service for user management operations.
     */
    @NonNull
    private final UserService userService;

    /**
     * Service for managing marketplace products.
     */
    @NonNull
    private final MarketPlaceService marketPlaceService;

    /**
     * Catalog menu component for product catalog operations.
     */
    @NonNull
    private final CatalogMenu catalogMenu;

    /**
     * Console view service for displaying user profiles.
     */
    @NonNull
    private final UserConsoleViewService userConsoleViewService;

    /**
     * Service for managing user shopping buckets.
     */
    @NonNull
    private final BucketService bucketService;

    /**
     * Service for handling customer orders.
     */
    @NonNull
    private final OrderService orderService;

    /**
     * Service for product category operations.
     */
    @NonNull
    private final CategoryService categoryService;

    /**
     * Console view service for displaying products.
     */
    @NonNull
    private final ProductsConsoleViewService productsConsoleViewService;

    /**
     * Console view service for displaying bucket contents.
     */
    @NonNull
    private final BucketConsoleViewService bucketConsoleViewService;

    /**
     * Console view service for displaying order history.
     */
    @NonNull
    private final OrderConsoleViewService orderConsoleViewService;

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

    private void handleAddProduct(final Scanner scanner) {
        var categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            System.out.println("Error: No categories found in the system! "
                    + "Please add categories first.");
            return;
        }

        System.out.println("\n--- Available Categories ---");
        categories.forEach(c -> System.out.println("ID: " + c.getId()
                + " | Name: " + c.getName()));

        long categoryId;
        while (true) {
            categoryId = InputValidator.getValidId(
                    scanner, "Enter category ID for the product:"
            );
            if (categoryService.isCategoryExists(categoryId)) {
                break;
            }
            System.out.println("Category with ID " + categoryId
                    + " does not exist! Try again.");
        }

        ProductCreateDto newProduct = new ProductCreateDto();
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

    private void handleBrowseProducts(final Scanner scanner) {
        var products = marketPlaceService.getAllProducts();
        productsConsoleViewService.catalogAllProducts(products);

        if (products.isEmpty()) {
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
                        option, scanner
                );
                case "4" -> handleRemoveProduct(scanner);
                case "5" -> handleEditProduct(scanner);
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void handleRemoveProduct(final Scanner scanner) {
        long id = InputValidator.getValidId(
                scanner, "Enter id of product to delete:"
        );
        try {
            List<UserDto> affectedUsers = marketPlaceService.removeProduct(id);
            System.out.println("Product successfully deleted!");

            if (!affectedUsers.isEmpty()) {
                System.out.println("This product was removed from the "
                        + "bucket of following users:");
                affectedUsers.forEach(user ->
                        System.out.printf(" - ID: %d | Name: %s %s%n",
                                user.getId(),
                                user.getFirstname(),
                                user.getLastname())
                );
            } else {
                System.out.println("No users had this product "
                        + "in their bucket.");
            }
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleEditProduct(final Scanner scanner) {
        long id = InputValidator.getValidId(
                scanner, "Enter id of product to edit:"
        );
        try {
            ProductDto product = marketPlaceService.getProductById(id);
            editProductInfoMenu(scanner, product.getId());
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleViewUsers(final Scanner scanner) {
        var users = userService.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("No users registered yet.");
            return;
        }

        System.out.println("All users:");
        users.forEach(userConsoleViewService::printUserProfile);

        long id = InputValidator.getValidId(
                scanner, "Enter user's id to view:"
        );
        try {
            UserDto user = userService.getUserById(id);
            handleUserSubMenu(scanner, user);
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleUserSubMenu(final Scanner scanner,
                                   final UserDto user) {
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
                        bucketConsoleViewService.displayBucket(cart);
                    }
                }
                case "2" -> {
                    var history = orderService.getUserOrders(user.getId());
                    orderConsoleViewService.displayOrderHistory(history);
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void handleManageCategories(final Scanner scanner) {
        while (true) {
            System.out.println("\n--- Manage Categories ---");
            System.out.println("1) Add new Category \n2) View Categories \n"
                    + "0) Go Back");
            String option = scanner.nextLine();

            switch (option) {
                case "1" -> {
                    String name = InputValidator.getValidName(
                            scanner, "Category"
                    );
                    try {
                        CategoryCreateDto categoryCreateDto =
                                new CategoryCreateDto();
                        categoryCreateDto.setName(name);
                        categoryService.addNewCategory(categoryCreateDto);
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
            var categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("\nNo categories found in the system. "
                        + "Add some first!");
                return;
            }

            System.out.println("\n--- Available Categories ---");
            categories.forEach(c -> System.out.println("ID: " + c.getId()
                    + " | Name: " + c.getName()));
            System.out.println("----------------------------");

            System.out.println("1) Change Category \n2) Remove Category \n"
                    + "0) Go Back");
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
                        CategoryDto categoryDto = new CategoryDto();
                        categoryDto.setName(newName);

                        categoryService.editCategory(categoryDto, id);
                        System.out.println("Category successfully updated!");
                    } catch (IllegalArgumentException
                             | EntityNotFoundException e) {
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
                    } catch (IllegalStateException
                             | EntityNotFoundException e) {
                        System.out.println("\n[ERROR] " + e.getMessage());
                    }
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void editProductInfoMenu(final Scanner scanner,
                                     final long productId) {
        System.out.println("Edit \n1) Name \n2) Price \n3) Category");
        String option = scanner.nextLine();

        switch (option) {
            case "1" -> {
                System.out.println("Enter new name:");
                String newName = scanner.nextLine();
                marketPlaceService.updateProductField(
                        productId, p -> p.setName(newName)
                );
                System.out.println("Name successfully updated!");
            }
            case "2" -> {
                BigDecimal newPrice = InputValidator.getValidPrice(
                        scanner, "Enter new price:"
                );
                marketPlaceService.updateProductField(
                        productId, p -> p.setPrice(newPrice)
                );
                System.out.println("Price successfully updated!");
            }
            case "3" -> {
                var categories = categoryService.getAllCategories();
                System.out.println("\n--- Available Categories ---");
                categories.forEach(c -> System.out.println("ID: " + c.getId()
                        + " | Name: " + c.getName()));

                long newCategoryId;
                while (true) {
                    newCategoryId = InputValidator.getValidId(
                            scanner, "Enter new category ID:"
                    );
                    if (categoryService.isCategoryExists(newCategoryId)) {
                        break;
                    }
                    System.out.println("Category with ID " + newCategoryId
                            + " does not exist! Try again.");
                }

                Category category = categoryService.getCategoryEntityById(
                        newCategoryId
                );

                marketPlaceService.updateProductField(
                        productId, p -> p.setCategory(category)
                );
                System.out.println("Category successfully updated!");
            }
            default -> System.out.println("Invalid Option!");
        }
    }
}
