package com.bobocode;

import com.bobocode.entities.users.AbstractUser;
import com.bobocode.entities.users.Admin;
import com.bobocode.entities.users.Staff;
import com.bobocode.entities.users.User;
import com.bobocode.menus.AdminMenu;
import com.bobocode.menus.AuthMenu;
import com.bobocode.menus.StaffMenu;
import com.bobocode.menus.UserMenu;
import com.bobocode.services.system.SystemInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Manages the console session and application life cycle.
 */
@Component
@RequiredArgsConstructor
public class ConsoleSessionManager {

    /** Authentication menu handler. */
    private final AuthMenu authMenu;

    /** Administrator menu handler. */
    private final AdminMenu adminMenu;

    /** Staff menu handler. */
    private final StaffMenu staffMenu;

    /** Regular user menu handler. */
    private final UserMenu userMenu;

    /** System initializer for startup checks. */
    private final SystemInitializer systemInitializer;

    /**
     * Starts the main console interaction loop.
     */
    public void startSession() {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            // 1. Initialize system (check/create admin)
            systemInitializer.initializeSystem(scanner);

            // 2. Life cycle of the application
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
        }
    }
}
