package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Utility.InputValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Menu for managing the user's shopping bucket.
 */
@RequiredArgsConstructor
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
     * Handles user interaction for bucket operations.
     *
     * @param user    the current authenticated user
     * @param scanner the scanner for console input
     */
    public void handleBucket(final User user, final Scanner scanner) {
        while (true) {
            if (user.getBucket().getProductsInBucket().isEmpty()) {
                System.out.println("\nYour bucket is empty! "
                        + "Add some products first.");
                return;
            }
            // exception 400
            catalogMenu.catalogAllProducts(
                    user.getBucket().getProductsInBucket());

            System.out.println("\n1) Purchase Items \n2) Remove Item "
                    + "\n0) Go Back");
            switch (scanner.nextLine()) {
                case "1" -> {
                    checkout(scanner, user);
                    return;
                }
                case "2" -> {
                    try {
                        long id = InputValidator.getValidId(scanner, "Enter Product ID to remove:");
                        Product product = marketPlaceService.getProductById(id);
                        bucketService.removeProductFromBucket(
                                user.getBucket(), product);
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
        if (bucketService.getProductsFromBucket(
                user.getBucket()).isEmpty()) {
            System.out.println("Your bucket is empty! "
                    + "Add some products first.");
            return;
        }
        // exception 400
        Pattern cardPattern = Pattern.compile("^\\d{16}$");

        while (true) {
            System.out.println("Enter your card number:");
            String input = scanner.nextLine().replaceAll("\\s+", "");

            if (cardPattern.matcher(input).matches()) {
                break;
            }
            System.out.println("Bad card format try (**** **** **** ****, or without spaces)");
        }

        System.out.println("Processing...");
        System.out.println("Debiting of funds...");
        System.out.println("Success!!! Purchase was made!");

        user.getPurchaseHistory().add(user.getBucket());

        // give user new bucket to save purchase history
        user.setBucket(new com.bobocode.Entities.Products.Bucket());
    }
}
