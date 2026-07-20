package com.bobocode;

import com.bobocode.Entities.Menus.*;
import com.bobocode.Entities.Products.MarketPlace;
import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Admin;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Entities.Users.User;
import com.bobocode.Services.Products.*;
import com.bobocode.Services.User.*;
import com.bobocode.Utility.EmailValidator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        MarketPlace marketPlace = new MarketPlace();
        Map<Long, AbstractUser> allUsers = new LinkedHashMap<>();


        UserService userService = new UserService(allUsers);
        StaffService staffService = new StaffService(allUsers);
        MarketPlaceService marketPlaceService = new MarketPlaceService(marketPlace);
        BucketService bucketService = new BucketService();
        FilterProductsService filterProductsService = new FilterProductsService();
        SortProductsService sortProductsService = new SortProductsService();
        AuthService authService = new AuthService(allUsers);
        UserConsoleViewService userConsoleViewService = new UserConsoleViewService();

        AuthMenu authMenu = new AuthMenu(authService, userService);
        AdminMenu adminMenu = new AdminMenu(staffService, userService);

        CatalogMenu catalogMenu = new CatalogMenu(marketPlaceService, filterProductsService, sortProductsService);
        StaffMenu staffMenu = new StaffMenu(userService, marketPlaceService, catalogMenu, userConsoleViewService);
        BucketMenu bucketMenu = new BucketMenu(bucketService, marketPlaceService, catalogMenu);
        UserMenu userMenu = new UserMenu(userService,bucketService,marketPlaceService,catalogMenu,userConsoleViewService,bucketMenu);


        System.out.println("--- SYSTEM SETUP: CREATE FIRST ADMIN ---");
        Admin firstAdmin = new Admin();
        firstAdmin.setId(AbstractUser.globalIdCounter++);

        System.out.println("Enter first name: ");
        firstAdmin.setFirstName(scanner.nextLine());

        System.out.println("Enter last name: ");
        firstAdmin.setLastName(scanner.nextLine());

        firstAdmin.setEmail(EmailValidator.getValidEmailFromConsole(scanner));

        System.out.println("Enter password: ");
        firstAdmin.setPassword(scanner.nextLine());

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
            }
            else if (loggedInUser instanceof Staff) {
                staffMenu.menu(scanner);
            }
            else if (loggedInUser instanceof User user) {
                userMenu.menu(user, scanner);
            }
        }

        scanner.close();
    }
}