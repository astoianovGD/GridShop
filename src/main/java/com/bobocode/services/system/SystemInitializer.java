package com.bobocode.services.system;

import com.bobocode.entities.users.Admin;
import com.bobocode.utility.EmailValidator;
import com.bobocode.utility.InputValidator;
import com.bobocode.utility.CustomJdbcTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class SystemInitializer {

    /** The JDBC template for database operations. */
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Checks if an admin exists, and if not, prompts to create the first admin.
     *
     * @param scanner the scanner for reading console input
     */
    public void initializeSystem(final Scanner scanner) {
        String checkAdminSql =
                "SELECT EXISTS (SELECT 1 FROM users WHERE role_id = ?)";
        Boolean adminExists = customJdbcTemplate.findOne(checkAdminSql, rs -> {
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
            customJdbcTemplate.execute(adminSql,
                    firstAdmin.getEmail(),
                    firstAdmin.getPassword(),
                    firstAdmin.getLastName(),
                    firstAdmin.getFirstName(),
                    1
            );
            System.out.println("Admin successfully created in Database!\n");
        }
    }
}
