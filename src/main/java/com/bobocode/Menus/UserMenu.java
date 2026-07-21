package com.bobocode.Menus;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;
import com.bobocode.Utility.EmailValidator;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;


@RequiredArgsConstructor
public class UserMenu {
    private final UserService userService;
    private final BucketService bucketService;
    private final MarketPlaceService marketPlaceService;
    private final CatalogMenu catalogMenu;
    private final UserConsoleViewService userConsoleViewService;
    private final BucketMenu bucketMenu;

    public void menu(User user, Scanner scanner) {
        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1) Browse Products \n2) View Personal Data \n3) View Bucket \n4) Delete Account \n0) Sign Out");
            switch (scanner.nextLine()) {
                case "1" -> {
                    catalogMenu.catalogAllProducts(marketPlaceService.getAllProducts());

                    while (true) {
                        System.out.println("Wanna do anything else?");
                        System.out.println("1) Sort \n2) Filter \n3) Search \n4) Add to Bucket \n0) Nothing");
                        String option = scanner.nextLine();
                        if ("0".equals(option)) break;

                        switch (option) {
                            case "1", "2", "3" -> catalogMenu.handleOptions(option, scanner);
                            case "4" -> {
                                System.out.println("Enter id of product to add to Bucket: ");
                                try {
                                    long id = Long.parseLong(scanner.nextLine());
                                    Product productToAdd = marketPlaceService.getProductById(id);
                                    bucketService.addProductToBucket(user.getBucket(), productToAdd);
                                    System.out.println("Product added to bucket!");
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid ID format! Please enter a number.");
                                } catch (EntityNotFoundException e) {
                                    System.out.println(e.getMessage());
                                }
                            }
                            default -> System.out.println("Invalid option!");
                        }
                    }
                }
                case "2" -> {
                    userConsoleViewService.printUserProfile(user);

                    while (true) {
                        System.out.println("\nWanna do anything else?");
                        System.out.println("1) Edit profile \n2) See Purchase History \n0) Nothing");

                        String option = scanner.nextLine();
                        if (option.equals("0")) break;
                        switch (option) {
                            case "1" -> editUserProfile(scanner, user);
                            case "2" -> userConsoleViewService.printUserPurchaseHistory(user);
                            default -> System.out.println("Invalid option!");
                        }
                    }

                }
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
                //400
            }
        }

    }

    private void editUserProfile(Scanner scanner, User user) {
        System.out.println("\n--- Edit Profile ---");
        System.out.println("Select field to change:");
        System.out.println("1) First Name \n2) Last Name \n3) Age \n4) Gender \n5) Email \n6) Password\n0) Cancel");

        String option = scanner.nextLine();

        switch (option) {
            case "1" -> {
                System.out.println("Enter new First Name:");
                user.setFirstName(scanner.nextLine());
                System.out.println("First Name updated!");
            }
            case "2" -> {
                System.out.println("Enter new Last Name:");
                user.setLastName(scanner.nextLine());
                System.out.println("Last Name updated!");
            }
            case "3" -> {
                System.out.println("Enter new Age:");
                try {
                    user.setAge(Integer.parseInt(scanner.nextLine()));
                    System.out.println("Age updated!");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid number.");
                }
            }
            case "4" -> {
                System.out.println("Enter your Gender (MALE, FEMALE, OTHER):");
                Gender gender;

                while (true) {
                    try {
                        gender = Gender.valueOf(scanner.nextLine().toUpperCase());
                        break;

                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid gender! Please enter exactly MALE, FEMALE, or OTHER:");
                    }
                }
                user.setGender(gender);
            }
            case "5" -> {
                String email = EmailValidator.getUniqueEmailFromConsole(scanner, userService);
                user.setEmail(email);
            }
            case "6" -> {
                System.out.println("Enter new Password ");
                user.setPassword(scanner.nextLine());
            }
            case "0" -> System.out.println("Editing cancelled.");
            default -> System.out.println("Invalid option!");
            //400
        }

        userService.editPersonalInformation(user.getId(), user);
    }
}
