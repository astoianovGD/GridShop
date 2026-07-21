package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.Staff;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Service for managing staff members.
 */
@RequiredArgsConstructor
public final class StaffService {

    /** A map containing all registered users. */
    @NonNull
    private final Map<Long, AbstractUser> allUsers;

    /**
     * Adds a new staff member.
     *
     * @param newStaff the staff member to add
     */
    public void addNewStaff(final Staff newStaff) {
        newStaff.setId(AbstractUser.generateNextId());
        allUsers.put(newStaff.getId(), newStaff);
    }

    /**
     * Edits an existing staff member.
     *
     * @param id          the ID of the staff member to edit
     * @param editedStaff the updated staff member details
     */
    public void editStaff(final long id, final Staff editedStaff) {
        if (allUsers.containsKey(id)) {
            allUsers.put(id, editedStaff);
        }
    }

    /**
     * Removes a staff member by ID.
     *
     * @param id the ID of the staff member to remove
     * @throws EntityNotFoundException  if the staff member is not found
     * @throws IllegalArgumentException if the user is not a staff member
     */
    public void removeStaff(final long id) {
        AbstractUser user = allUsers.get(id);

        if (user == null) {
            throw new EntityNotFoundException("Staff with ID " + id
                    + " not found!");
        }

        if (!(user instanceof Staff)) {
            throw new IllegalArgumentException("User with this ID is not a "
                    + "Staff member!");
        }

        allUsers.remove(id);
    }

    /**
     * Retrieves a list of all staff members.
     *
     * @return a list of all staff members
     */
    public List<Staff> getAllStaff() {
        return allUsers.values().stream()
                .filter(Staff.class::isInstance)
                .map(Staff.class::cast)
                .toList();
    }

    /**
     * Retrieves a staff member by ID.
     *
     * @param id the ID of the staff member to retrieve
     * @return the staff member
     * @throws EntityNotFoundException if the staff member is not found
     */
    public Staff getStaffById(final long id) {
        AbstractUser user = allUsers.get(id);

        if (user instanceof Staff staff) {
            return staff;
        }

        throw new EntityNotFoundException("HTTP STATUS 404 : Staff with ID "
                + id + " not found!");
    }
}
