package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Admin;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Entities.Users.User;
import com.bobocode.Enums.Gender;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Utility.JdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

/**
 * Service for handling user authentication.
 */
@RequiredArgsConstructor
public final class AuthService {

    /** The JDBC template for database operations. */
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    /**
     * Signs a user in using their email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the authenticated user
     * @throws EntityNotFoundException if credentials are invalid
     */
    public AbstractUser signIn(final String email, final String password) {
        System.out.println("Searching in base...");

        String sql = "SELECT u.user_id, u.email, u.password, "
                + "u.lastname, u.firstname, u.age, u.gender, "
                + "r.name as role_name "
                + "FROM users u "
                + "JOIN roles r ON u.role_id = r.role_id "
                + "WHERE u.email = ? AND u.password = ?";

        AbstractUser user = jdbcTemplate.findOne(sql, rs -> {
            try {
                String roleName = rs.getString("role_name");
                AbstractUser u;
                if ("ADMIN".equalsIgnoreCase(roleName)) {
                    u = new Admin();
                } else if ("STAFF".equalsIgnoreCase(roleName)) {
                    u = new Staff();
                } else {
                    u = new User();
                }

                u.setId(rs.getLong("user_id"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setLastName(rs.getString("lastname"));
                u.setFirstName(rs.getString("firstname"));

                if (u instanceof User regularUser) {
                    regularUser.setAge(rs.getInt("age"));
                    String genderStr = rs.getString("gender");
                    if (genderStr != null) {
                        regularUser.setGender(Gender.valueOf(genderStr));
                    }
                }

                return u;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping User during sign in", e
                );
            }
        }, email, password);

        if (user == null) {
            throw new EntityNotFoundException(
                    "Error: Invalid email or password! Please try again.");
        }

        // activating account
        String restoreSql = "UPDATE users SET is_active = true "
                + "WHERE user_id = ? AND is_active = false";
        jdbcTemplate.execute(restoreSql, user.getId());

        return user;
    }
}
