package com.bobocode.Entities.Users;

import lombok.Data;

/**
 * Base abstract class for all users in the system.
 */
@Data
public abstract class AbstractUser {

    /**
     * Global counter for generating unique user IDs.
     */
    private static long globalIdCounter = 1;

    /**
     * Unique identifier for the user.
     */
    private long id;

    /**
     * The user's password.
     */
    private String password;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * Generates and returns the next available global user ID.
     *
     * @return the new generated ID
     */
    public static long generateNextId() {
        return globalIdCounter++;
    }

    /**
     * Returns a formatted string representation of the user.
     * Subclasses can override this method to add specific fields,
     * but should maintain the basic layout.
     *
     * @return a string containing the user's role, ID, name, and email
     */
    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Name: %s %s | Email: %s",
                this.getClass().getSimpleName(),
                id,
                firstName,
                lastName,
                email);
    }
}
