package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Entities.Users.User;
import com.bobocode.Exceptions.EmailAlreadyExistsException;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Service class for managing standard users.
 */
@RequiredArgsConstructor
public final class UserService {

    /** Map storing all registered users in the system. */
    private final Map<Long, AbstractUser> allUsers;

    /**
     * Registers a new user in the system.
     *
     * @param newUser the user to register
     */
    public void registerNewUser(final User newUser) {
        newUser.setId(AbstractUser.generateNextId());
        allUsers.put(newUser.getId(), newUser);
    }

    /**
     * Deletes a user account by its ID.
     *
     * @param id the ID of the user to delete
     * @throws IllegalArgumentException if the ID does not belong to a User
     */
    public void deleteUserAccount(final long id) {
        if (allUsers.get(id) instanceof User) {
            allUsers.remove(id);
        } else {
            throw new IllegalArgumentException(
                    "Entity with this ID is not a User!");
        }
    }

    /**
     * Updates the personal information of an existing user.
     *
     * @param id          the ID of the user to update
     * @param changedUser the user object containing updated information
     */
    public void editPersonalInformation(
            final long id, final User changedUser) {
        allUsers.put(id, changedUser);
    }

    /**
     * Retrieves a list of all registered users.
     *
     * @return a list containing all users
     */
    public List<User> getAllUsers() {
        return allUsers.values().stream()
                .filter(User.class::isInstance)
                .map(User.class::cast)
                .toList();
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the requested user
     * @throws EntityNotFoundException if the user is not found
     */
    public User getUserById(final long id) {
        if (allUsers.get(id) instanceof User user) {
            return user;
        }
        throw new EntityNotFoundException("User with ID " + id
                + " not found!");
    }

    /**
     * Checks if a given email is already taken.
     *
     * @param email the email to check
     * @return true if the email is registered, false otherwise
     */
    public boolean isEmailTaken(final String email) {
        return allUsers.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
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
                            + "Please try another one.");
        }
    }
}
