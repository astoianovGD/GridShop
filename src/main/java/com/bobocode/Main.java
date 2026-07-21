package com.bobocode;

import com.bobocode.Entities.Products.MarketPlace;
import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Admin;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Entities.Users.User;

import com.bobocode.Menus.AdminMenu;
import com.bobocode.Menus.AuthMenu;
import com.bobocode.Menus.BucketMenu;
import com.bobocode.Menus.CatalogMenu;
import com.bobocode.Menus.StaffMenu;
import com.bobocode.Menus.UserMenu;

import com.bobocode.Services.Products.BucketService;
import com.bobocode.Services.Products.FilterProductsService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.SortProductsService;

import com.bobocode.Services.User.AuthService;
import com.bobocode.Services.User.StaffService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;

import com.bobocode.Utility.EmailValidator;
import com.bobocode.Utility.InputValidator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Main application class.
 */
public final class Main {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private Main() {
    }

    /**
     * The main entry point of the application.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        Scanner scanner = new Scanner(
                System.in, java.nio.charset.StandardCharsets.UTF_8
        );

        MarketPlace marketPlace = new MarketPlace();
        Map<Long, AbstractUser> allUsers = new LinkedHashMap<>();

        UserService userService = new UserService(allUsers);
        StaffService staffService = new StaffService(allUsers);
        MarketPlaceService marketPlaceService =
                new MarketPlaceService(marketPlace);
        BucketService bucketService = new BucketService();
        FilterProductsService filterProductsService =
                new FilterProductsService();
        SortProductsService sortProductsService = new SortProductsService();
        AuthService authService = new AuthService(allUsers);
        UserConsoleViewService userConsoleViewService =
                new UserConsoleViewService();

        AuthMenu authMenu = new AuthMenu(authService, userService);
        AdminMenu adminMenu = new AdminMenu(staffService, userService);


        CatalogMenu catalogMenu = new CatalogMenu(
                marketPlaceService, filterProductsService, sortProductsService);

        StaffMenu staffMenu = new StaffMenu(
                userService, marketPlaceService, catalogMenu,
                userConsoleViewService);

        BucketMenu bucketMenu = new BucketMenu(
                bucketService, marketPlaceService, catalogMenu);

        UserMenu userMenu = new UserMenu(
                userService, bucketService, marketPlaceService,
                catalogMenu, userConsoleViewService, bucketMenu);

        System.out.println("--- SYSTEM SETUP: CREATE FIRST ADMIN ---");
        Admin firstAdmin = new Admin();
        firstAdmin.setId(AbstractUser.generateNextId());

        firstAdmin.setFirstName(InputValidator.getValidName(
                scanner, "First Name")
        );

        firstAdmin.setLastName(InputValidator.getValidName(
                scanner, "Last Name")
        );

        firstAdmin.setEmail(EmailValidator.getValidEmailFromConsole(scanner));

        firstAdmin.setPassword(InputValidator.getValidPassword(scanner));

        allUsers.put(firstAdmin.getId(), firstAdmin);
        System.out.println("Admin successfully created!\n");

        while (true) {
            AbstractUser loggedInUser = authMenu.menu(scanner);

            if (loggedInUser == null) {
                System.out.println("Shutting down the system...");
                break;
            }

            if (loggedInUser instanceof Admin) {
                adminMenu.menu(scanner);
            } else if (loggedInUser instanceof Staff) {
                staffMenu.menu(scanner);
            } else if (loggedInUser instanceof User user) {
                userMenu.menu(user, scanner);
            }
        }

        scanner.close();
    }
}
