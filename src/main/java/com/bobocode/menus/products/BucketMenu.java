package com.bobocode.menus.products;

import com.bobocode.dto.bucket.BucketItemDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.services.bucket.BucketConsoleViewService;
import com.bobocode.services.bucket.BucketService;
import com.bobocode.services.orders.OrderService;
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
     * Service for orders.
     */
    @NonNull
    private final OrderService orderService;

    @NonNull
    private final BucketConsoleViewService bucketConsoleViewService;


    /**
     * Handles user interaction for bucket operations.
     *
     * @param user    the current authenticated user
     * @param scanner the scanner for console input
     */
    public void handleBucket(final UserDto user, final Scanner scanner) {
        while (true) {
            List<BucketItemDto> userBucketProducts = bucketService.
                    getProductsFromBucket(user.getId());

            if (userBucketProducts.isEmpty()) {
                System.out.println("\nYour bucket is empty! "
                        + "Add some products first.");
                return;
            }

            bucketConsoleViewService.displayBucket(userBucketProducts);

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
    private void checkout(final Scanner scanner, final UserDto user) {
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
    }
}

