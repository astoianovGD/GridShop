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

@RequiredArgsConstructor
public class StaffMenu {
    private final UserService userService;
    private final MarketPlaceService marketPlaceService;
    private final CatalogMenu catalogMenu;
    private final UserConsoleViewService userConsoleViewService;

    public void menu(Scanner scanner) {
        while(true) {
            System.out.println("\n--- Staff Menu ---");
            System.out.println("1) Add Product \n2) Browse Products \n3) View User Profile \n0) Sign out");
            switch (scanner.nextLine()) {
                case "1" -> {
                    Product newProduct = new Product();
                    System.out.println("Enter product name: ");
                    newProduct.setName(scanner.nextLine());
                    System.out.println("Enter product price: ");
                    newProduct.setPrice(new BigDecimal(scanner.nextLine()));
                    marketPlaceService.addNewProduct(newProduct);
                }
                case "2" -> {
                    catalogMenu.catalogAllProducts(marketPlaceService.getAllProducts());

                    while (true) {
                        System.out.println("\nWanna do smth else?");
                        System.out.println("1) Sort \n2) Filter \n3) Search \n4) Remove \n5) Edit \n0) Nothing");
                        String option = scanner.nextLine();
                        if (option.equals("0")) break;
                        switch (option){
                            case "1", "2", "3" -> catalogMenu.handleOptions(option, scanner);
                            case "4" -> {
                                System.out.println("Enter id of product to delete: ");
                                try {
                                    long id = Long.parseLong(scanner.nextLine());
                                    marketPlaceService.removeProduct(id);
                                    System.out.println("Product successfully deleted!");
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid ID format! Please enter a valid number.");
                                } catch (EntityNotFoundException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            case "5" -> {
                                System.out.println("Enter id of product to edit: ");
                                try {
                                    long id = Long.parseLong(scanner.nextLine());
                                    Product product = marketPlaceService.getProductById(id);

                                    editProductInfoMenu(scanner, product);

                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid ID format! Please enter a valid number.");
                                } catch (EntityNotFoundException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                    }

                }
                case "3" -> {
                    var users = userService.getAllUsers();
                    if (users.isEmpty()) {
                        System.out.println("No users registered yet.");
                        break;
                    }

                    System.out.println("All users:");
                    users.forEach(System.out::println);

                    System.out.println("Enter user's id to view: ");
                    try {
                        long id = Long.parseLong(scanner.nextLine());
                        User user = userService.getUserById(id);

                        while (true) {
                            userConsoleViewService.printUserProfile(user);

                            System.out.println("\nWanna view more?");
                            System.out.println("1) View user's cart \n2) View user's purchase history \n0) Return to menu");
                            String option = scanner.nextLine();
                            if ("0".equals(option)) break;
                            switch (option) {
                                case "1" -> {
                                    var cart = user.getBucket().getProductsInBucket();
                                    if (cart.isEmpty()) System.out.println("Cart is empty.");
                                    else cart.forEach(System.out::println);
                                }
                                case "2" -> {
                                    if (user.getPurchaseHistory().isEmpty()) {
                                        System.out.println("History is empty.");
                                    } else {
                                        user.getPurchaseHistory().forEach(System.out::println);
                                    }
                                }
                                default -> System.out.println("Invalid option!");
                                //400
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format! Please enter a valid number.");
                    } catch (EntityNotFoundException e) {
                        System.out.println(e.getMessage());
                    }

                }
                case "0" -> { return; }
                default -> System.out.println("Invalid option!");
                //400
            }
        }
    }

    private void editProductInfoMenu(Scanner scanner, Product product) {
        System.out.println("Edit \n1) Name \n2) Price");
        String option = scanner.nextLine();
        if (option.equals("1")) {
            System.out.println("Enter new name: ");
            product.setName(scanner.nextLine());
        }
        else if(option.equals("2")) {
            System.out.println("Enter new price: ");
            try {
                product.setPrice(new BigDecimal(scanner.nextLine()));
                System.out.println("Price successfully updated!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format! Please enter a valid number.");
            }
        }
        else {
            System.out.println("Invalid Option!");
            //400
        }
    }
}