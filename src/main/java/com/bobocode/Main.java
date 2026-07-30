package com.bobocode;

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
import com.bobocode.Services.Products.CategoryService;
import com.bobocode.Services.Products.FilterProductsService;
import com.bobocode.Services.Products.MarketPlaceService;
import com.bobocode.Services.Products.OrderService;
import com.bobocode.Services.Products.SortProductsService;
import com.bobocode.Services.User.AuthService;
import com.bobocode.Services.User.StaffService;
import com.bobocode.Services.User.UserConsoleViewService;
import com.bobocode.Services.User.UserService;
import com.bobocode.Utility.EmailValidator;
import com.bobocode.Utility.InputValidator;
import com.bobocode.Utility.JdbcTemplate;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Main application class.
 */
public final class Main {

    private Main() {
    }

    /**
     * Main entry point for the application.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        Scanner scanner = new Scanner(
                System.in, java.nio.charset.StandardCharsets.UTF_8
        );

        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("POSTGRES_USER");
        String dbPassword = System.getenv("POSTGRES_PASSWORD");

        JdbcTemplate jdbcTemplate = new JdbcTemplate(
                dbUrl, dbUser, dbPassword
        );

        UserService userService = new UserService(jdbcTemplate);
        StaffService staffService = new StaffService(jdbcTemplate);
        BucketService bucketService = new BucketService(jdbcTemplate);
        MarketPlaceService marketPlaceService = new MarketPlaceService(
                jdbcTemplate
        );

        FilterProductsService filterProductsService =
                new FilterProductsService();
        SortProductsService sortProductsService =
                new SortProductsService();
        AuthService authService = new AuthService(jdbcTemplate);
        UserConsoleViewService userConsoleViewService =
                new UserConsoleViewService();

        AuthMenu authMenu = new AuthMenu(authService, userService);
        AdminMenu adminMenu = new AdminMenu(staffService, userService);

        CatalogMenu catalogMenu = new CatalogMenu(
                marketPlaceService, filterProductsService, sortProductsService);

        OrderService orderService = new OrderService(jdbcTemplate);

        CategoryService categoryService = new CategoryService(jdbcTemplate);

        StaffMenu staffMenu = new StaffMenu(
                userService,
                marketPlaceService,
                catalogMenu,
                userConsoleViewService,
                bucketService,
                orderService,
                categoryService);

        BucketMenu bucketMenu = new BucketMenu(
                bucketService,
                marketPlaceService,
                catalogMenu,
                orderService);

        UserMenu userMenu = new UserMenu(
                userService,
                bucketService,
                marketPlaceService,
                catalogMenu,
                userConsoleViewService,
                bucketMenu,
                orderService);

        String checkAdminSql =
                "SELECT EXISTS (SELECT 1 FROM users WHERE role_id = ?)";
        Boolean adminExists = jdbcTemplate.findOne(checkAdminSql, rs -> {
            try {
                return rs.getBoolean(1);
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error checking admin existence", e
                );
            }
        }, 1);

        if (adminExists != null && adminExists) {
            System.out.println(
                    "--- SYSTEM SETUP: Admin already exists "
                            + "in Database. Skipping setup. ---\n"
            );
        } else {
            System.out.println("--- SYSTEM SETUP: CREATE FIRST ADMIN ---");
            Admin firstAdmin = new Admin();
            firstAdmin.setFirstName(
                    InputValidator.getValidName(scanner, "First Name")
            );
            firstAdmin.setLastName(
                    InputValidator.getValidName(scanner, "Last Name")
            );
            firstAdmin.setEmail(
                    EmailValidator.getValidEmailFromConsole(scanner)
            );
            firstAdmin.setPassword(
                    InputValidator.getValidPassword(scanner)
            );

            String adminSql = "INSERT INTO users "
                    + "(email, password, lastname, firstname, role_id) "
                    + "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.execute(adminSql,
                    firstAdmin.getEmail(),
                    firstAdmin.getPassword(),
                    firstAdmin.getLastName(),
                    firstAdmin.getFirstName(),
                    1
            );
            System.out.println("Admin successfully created in Database!\n");
        }

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
