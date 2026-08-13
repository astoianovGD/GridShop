package com.bobocode.services.system;

import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import com.bobocode.utility.EmailValidator;
import com.bobocode.utility.InputValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Scanner;

/**
 * Service for initializing system default configurations like admin creation.
 */
@Service
@RequiredArgsConstructor
public class SystemInitializer {

    /**
     * Repository for managing user entities.
     */
    private final UserRepository userRepository;

    /**
     * Repository for managing role entities.
     */
    private final RoleRepository roleRepository;

    /**
     * Checks if an admin exists, and if not, prompts to create the first admin.
     *
     * @param scanner the scanner for reading console input
     */
    public void initializeSystem(final Scanner scanner) {
        boolean adminExists = userRepository.existsByRoleName("ADMIN");

        if (adminExists) {
            System.out.println(
                    "--- SYSTEM SETUP: Admin already exists "
                            + "in Database. Skipping setup. ---\n"
            );
        } else {
            System.out.println("--- SYSTEM SETUP: CREATE FIRST ADMIN ---");

            User firstAdmin = new User();
            firstAdmin.setFirstname(
                    InputValidator.getValidName(scanner, "First Name")
            );
            firstAdmin.setLastname(
                    InputValidator.getValidName(scanner, "Last Name")
            );
            firstAdmin.setEmail(
                    EmailValidator.getValidEmailFromConsole(scanner)
            );
            firstAdmin.setPassword(
                    InputValidator.getValidPassword(scanner)
            );

            firstAdmin.setActive(true);

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Default role 'ADMIN' not found in database!"
                    ));

            firstAdmin.setRole(adminRole);

            userRepository.save(firstAdmin);

            System.out.println("Admin successfully created in Database!\n");
        }
    }
}
