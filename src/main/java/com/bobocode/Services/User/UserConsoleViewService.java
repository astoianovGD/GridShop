package com.bobocode.Services.User;

import com.bobocode.Entities.Products.Bucket;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for displaying user information in the console.
 */
@RequiredArgsConstructor
public final class UserConsoleViewService {

    /**
     * Prints the profile of the specified user.
     *
     * @param user the user whose profile is to be printed
     */
    public void printUserProfile(final User user) {
        String fullName = user.getFirstName() + " " + user.getLastName();

        System.out.println("\n+--------------------------------------+");
        System.out.println("|             USER PROFILE             |");
        System.out.println("+--------------------------------------+");
        System.out.printf("| %-10s : %-23d |%n", "ID", user.getId());
        System.out.printf("| %-10s : %-23s |%n", "Name", fullName);
        System.out.printf("| %-10s : %-23d |%n", "Age", user.getAge());
        System.out.printf("| %-10s : %-23s |%n", "Gender", user.getGender());
        System.out.println("+--------------------------------------+\n");
    }

    /**
     * Prints the purchase history of the specified user.
     *
     * @param user the user whose purchase history is to be printed
     */
    public void printUserPurchaseHistory(final User user) {
        List<Bucket> history = user.getPurchaseHistory();

        if (history == null || history.isEmpty()) {
            System.out.println("History is empty. No purchases yet.");
            return;
        }

        int receiptNumber = 1;
        for (Bucket bucket : history) {
            System.out.println("\nReceipt #" + receiptNumber + ":");
            System.out.println("---------------------------------");

            BigDecimal totalAmount = BigDecimal.ZERO;

            for (Product product : bucket.getProductsInBucket()) {
                System.out.printf("- %-15s | $%5.2f%n",
                        product.getName(), product.getPrice());
                totalAmount = totalAmount.add(product.getPrice());
            }

            System.out.println("---------------------------------");
            System.out.printf("Total:            | $%5.2f%n", totalAmount);

            receiptNumber++;
        }
    }

}
