package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.MarketPlaceService;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class BucketMenu {
    private final BucketService bucketService;
    private final MarketPlaceService marketPlaceService;
    private final CatalogMenu catalogMenu;

    public void handleBucket(User user, Scanner scanner) {
        while (true) {
            if (user.getBucket().getProductsInBucket().isEmpty()) {
                System.out.println("\nYour bucket is empty! Add some products first.");
                return;
            }
            //exception 400
            catalogMenu.catalogAllProducts(user.getBucket().getProductsInBucket());

            System.out.println("\n1) Purchase Items \n2) Remove Item \n0) Go Back");
            switch (scanner.nextLine()) {
                case "1" -> {
                    checkout(scanner, user);
                    return;
                }
                case "2" -> {
                    System.out.println("Enter Product ID to remove:");
                    try {
                        long id = Long.parseLong(scanner.nextLine());
                        Product product = marketPlaceService.getProductById(id);
                        bucketService.removeProductFromBucket(user.getBucket(), product);
                        System.out.println("Product successfully removed!");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format! Please enter a number.");
                    } catch (EntityNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                }
                case "0" -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private void checkout(Scanner scanner, User user) {
        if (bucketService.getProductsFromBucket(user.getBucket()).isEmpty()) {
            System.out.println("Your bucket is empty! Add some products first.");
            return;
        }
        //exception 4й00

        System.out.println("Enter your card number:");
        scanner.nextLine();

        System.out.println("Processing...");
        System.out.println("Debiting of funds...");
        System.out.println("Success!!! Purchase was made!");

        user.getPurchaseHistory().add(user.getBucket());

        //give user new bucket to save purchase history
        user.setBucket(new com.bobocode.Entities.Products.Bucket());
    }
}