package com.bobocode.Services.User;

import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Utility.JdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing staff members.
 */
@RequiredArgsConstructor
public final class StaffService {

    /** The JDBC template for database operations. */
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    /**
     * Adds a new staff member.
     *
     * @param newStaff the staff member to add
     */
    public void addNewStaff(final Staff newStaff) {
        String sql = "INSERT INTO users "
                + "(email, password, lastname, "
                + "firstname, age, gender, role_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, "
                + "(SELECT role_id FROM roles WHERE name = 'STAFF'))";
        jdbcTemplate.execute(sql,
                newStaff.getEmail(),
                newStaff.getPassword(),
                newStaff.getLastName(),
                newStaff.getFirstName(),
                null,
                null
        );
    }

    /**
     * Edits an existing staff member.
     *
     * @param id          the ID of the staff member to edit
     * @param editedStaff the updated staff member details
     */
    public void editStaff(final long id, final Staff editedStaff) {
        getStaffById(id);

        String sql = "UPDATE users SET email = ?, password = ?, "
                + "lastname = ?, firstname = ? WHERE user_id = ?";
        jdbcTemplate.execute(sql,
                editedStaff.getEmail(),
                editedStaff.getPassword(),
                editedStaff.getLastName(),
                editedStaff.getFirstName(),
                id
        );
    }

    /**
     * Removes a staff member by ID.
     *
     * @param id the ID of the staff member to remove
     * @throws EntityNotFoundException if the staff member is not found
     */
    public void removeStaff(final long id) {
        getStaffById(id);

        String sql = "UPDATE users SET is_active = false WHERE user_id = ?";
        jdbcTemplate.execute(sql, id);
    }

    /**
     * Retrieves a list of all staff members.
     *
     * @return a list of all staff members
     */
    public List<Staff> getAllStaff() {
        String sql = "SELECT user_id, email, password, "
                + "lastname, firstname, role_id "
                + "FROM users "
                + "WHERE role_id = "
                + "(SELECT role_id FROM roles WHERE name = 'STAFF') "
                + "AND is_active = true";

        return jdbcTemplate.findMany(sql, rs -> {
            try {
                Staff staff = new Staff();
                staff.setId(rs.getLong("user_id"));
                staff.setEmail(rs.getString("email"));
                staff.setPassword(rs.getString("password"));
                staff.setLastName(rs.getString("lastname"));
                staff.setFirstName(rs.getString("firstname"));
                return staff;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Staff from ResultSet", e
                );
            }
        });
    }

    /**
     * Retrieves a staff member by ID.
     *
     * @param id the ID of the staff member to retrieve
     * @return the staff member
     * @throws EntityNotFoundException if the staff member is not found
     */
    public Staff getStaffById(final long id) {
        String sql = "SELECT user_id, email, password, "
                + "lastname, firstname, role_id "
                + "FROM users "
                + "WHERE user_id = ? AND role_id = "
                + "(SELECT role_id FROM roles WHERE name = 'STAFF') "
                + "AND is_active = true";

        Staff staff = jdbcTemplate.findOne(sql, rs -> {
            try {
                Staff s = new Staff();
                s.setId(rs.getLong("user_id"));
                s.setEmail(rs.getString("email"));
                s.setPassword(rs.getString("password"));
                s.setLastName(rs.getString("lastname"));
                s.setFirstName(rs.getString("firstname"));
                return s;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Staff from ResultSet", e
                );
            }
        }, id);

        if (staff == null) {
            throw new EntityNotFoundException(
                    "HTTP STATUS 404 : Staff with ID "
                            + id + " not found!"
            );
        }

        return staff;
    }
}
