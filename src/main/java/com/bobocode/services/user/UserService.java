package com.bobocode.services.user;

import com.bobocode.entities.users.User;
import com.bobocode.enums.Gender;
import com.bobocode.exceptions.EmailAlreadyExistsException;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.utility.CustomJdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class for managing standard users.
 */
@RequiredArgsConstructor
@Service
public final class UserService {

    /** The JDBC template for database operations. */
    @NonNull
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Registers a new user in the system.
     *
     * @param newUser the user to register
     */
    public void registerNewUser(final User newUser) {
        String insertUserSql =
                "INSERT INTO users "
                        + "(email, password, lastname, firstname, "
                        + "age, gender, role_id) VALUES (?, ?, ?, ?, ?, ?, "
                        + "(SELECT role_id FROM roles WHERE name = 'USER'))";

        String genderStr = newUser.getGender() != null
                ? newUser.getGender().name()
                : null;

        // make insert by execute
        customJdbcTemplate.execute(insertUserSql,
                newUser.getEmail(),
                newUser.getPassword(),
                newUser.getLastName(),
                newUser.getFirstName(),
                newUser.getAge(),
                genderStr
        );

        // get id by email
        String getIdSql = "SELECT user_id FROM users WHERE email = ?";
        Long userId = customJdbcTemplate.findOne(getIdSql, rs -> {
            try {
                return rs.getLong("user_id");
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error getting user_id after registration", e
                );
            }
        }, newUser.getEmail());

        if (userId != null) {
            String createBucketSql =
                    "INSERT INTO bucket (user_id) VALUES (?)";
            customJdbcTemplate.execute(createBucketSql, userId);
        }
    }

    /**
     * Deletes a user account by its ID.
     *
     * @param id the ID of the user to delete
     */
    public void deleteUserAccount(final long id) {
        getUserById(id);

        String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
        customJdbcTemplate.execute(sql, id);
    }

    /**
     * Updates the personal information of an existing user.
     *
     * @param id          the ID of the user to update
     * @param changedUser the user object containing updated information
     */
    public void editPersonalInformation(
            final long id, final User changedUser) {
        getUserById(id);

        String sql = "UPDATE users SET email = ?, password = ?, "
                + "lastname = ?, firstname = ?, age = ?, gender = ? "
                + "WHERE user_id = ?";

        String genderStr = changedUser.getGender() != null
                ? changedUser.getGender().name()
                : null;

        customJdbcTemplate.execute(sql,
                changedUser.getEmail(),
                changedUser.getPassword(),
                changedUser.getLastName(),
                changedUser.getFirstName(),
                changedUser.getAge(),
                genderStr,
                id
        );
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return a list containing all users
     */
    public List<User> getAllUsers() {
        String sql = "SELECT u.user_id, u.email, u.password, "
                + "u.lastname, u.firstname, u.age, u.gender, u.role_id "
                + "FROM users u "
                + "JOIN roles r ON u.role_id = r.role_id "
                + "WHERE r.name = 'USER' AND u.is_active = true";

        return customJdbcTemplate.findMany(sql, this::mapUserRow);
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the requested user
     * @throws EntityNotFoundException if the user is not found
     */
    public User getUserById(final long id) {
        String sql = "SELECT user_id, email, password, "
                + "lastname, firstname, age, gender, role_id "
                + "FROM users "
                + "WHERE user_id = ? "
                + "AND role_id = "
                + "(SELECT role_id FROM roles WHERE name = 'USER') "
                + "AND is_active = true";

        User user = customJdbcTemplate.findOne(sql, this::mapUserRow, id);

        if (user == null) {
            throw new EntityNotFoundException(
                    "User with ID " + id + " not found!"
            );
        }

        return user;
    }

    /**
     * Checks if a given email is already taken.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    public boolean isEmailTaken(final String email) {
        String sql = "SELECT EXISTS (SELECT 1 FROM users WHERE email = ?)";

        Boolean exists = customJdbcTemplate.findOne(sql, rs -> {
            try {
                return rs.getBoolean(1);
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error checking if email is taken", e
                );
            }
        }, email);

        return exists != null && exists;
    }

    /**
     * Validates that the provided email is not already registered.
     *
     * @param email the email to validate
     * @throws EmailAlreadyExistsException if the email is already in use
     */
    public void validateEmailIsFree(final String email) {
        if (isEmailTaken(email)) {
            throw new EmailAlreadyExistsException(
                    "Error 409: This email is already registered! "
                            + "Please try another one."
            );
        }
    }

    /**
     * Maps a ResultSet row to a User object.
     *
     * @param rs the result set row
     * @return the mapped user object
     */
    private User mapUserRow(final ResultSet rs) {
        try {
            User u = new User();
            u.setId(rs.getLong("user_id"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setLastName(rs.getString("lastname"));
            u.setFirstName(rs.getString("firstname"));
            u.setAge(rs.getInt("age"));

            String genderStr = rs.getString("gender");
            if (genderStr != null) {
                u.setGender(Gender.valueOf(genderStr));
            }
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping User from ResultSet", e);
        }
    }
}
