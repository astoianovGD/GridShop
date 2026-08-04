package com.bobocode.menus;

import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.services.products.BucketService;
import com.bobocode.services.products.MarketPlaceService;
import com.bobocode.services.products.OrderService;
import com.bobocode.utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Menu for managing the user's shopping bucket.
 */
@RequiredArgsConstructor
@Component
public final class BucketMenu {

    /**
     * Service for handling bucket operations.
     */
    @NonNull
    private final BucketService bucketService;

    /**
     * Service for marketplace interactions.
     */
    @NonNull
    private final MarketPlaceService marketPlaceService;

    /**
     * Menu for displaying product catalog.
     */
    @NonNull
    private final CatalogMenu catalogMenu;

    /**
     * Service for orders.
     */
    @NonNull
    private final OrderService orderService;

    /**
     * Handles user interaction for bucket operations.
     *
     * @param user    the current authenticated user
     * @param scanner the scanner for console input
     */
    public void handleBucket(final User user, final Scanner scanner) {
        while (true) {
            List<Product> userBucketProducts = bucketService.
                    getProductsFromBucket(user.getId());

            if (userBucketProducts.isEmpty()) {
                System.out.println("\nYour bucket is empty! "
                        + "Add some products first.");
                return;
            }

            catalogMenu.catalogAllProducts(userBucketProducts);

            System.out.println("\n1) Purchase Items \n2) Remove Item "
                    + "\n0) Go Back");
            switch (scanner.nextLine()) {
                case "1" -> {
                    checkout(scanner, user);
                    return;
                }
                case "2" -> {
                    try {
                        long id = InputValidator.getValidId(
                                scanner, "Enter Product ID to remove:"
                        );

                        bucketService.removeProductFromBucket(user.getId(), id);
                        System.out.println("Product successfully removed!");
                    } catch (EntityNotFoundException e) {
                        System.out.println(e.getMessage());
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
     * Processes the checkout for the user's bucket.
     *
     * @param scanner the scanner for console input
     * @param user    the user performing the checkout
     */
    private void checkout(final Scanner scanner, final User user) {
        if (bucketService.getProductsFromBucket(user.getId()).isEmpty()) {
            System.out.println("Your bucket is empty! "
                    + "Add some products first.");
            return;
        }

        Pattern cardPattern = Pattern.compile("^\\d{16}$");

        while (true) {
            System.out.println("Enter your card number:");
            String input = scanner.nextLine().replaceAll("\\s+", "");

            if (cardPattern.matcher(input).matches()) {
                break;
            }
            System.out.println("Bad card format try "
                    + "(**** **** **** ****, or without spaces)");
        }

        System.out.println("Processing...");
        System.out.println("Debiting of funds...");

        orderService.createOrderFromBucket(user.getId());

        System.out.println("Success!!! "
                + "Purchase was made and saved to your history!");

        if (user.getBucket() != null
                && user.getBucket().getProductsInBucket() != null) {
            user.getBucket().getProductsInBucket().clear();
        }
    }
}

