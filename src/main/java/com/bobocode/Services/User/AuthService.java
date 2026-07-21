package com.bobocode.Services.User;

import com.bobocode.Entities.Users.AbstractUser;
import com.bobocode.Exceptions.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Service for handling user authentication.
 */
@RequiredArgsConstructor
public final class AuthService {

    /** A map of all registered users. */
    @NonNull
    private final Map<Long, AbstractUser> allUsers;

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
        return allUsers.values().stream()
                .filter(user -> user.getEmail().equals(email)
                        && user.getPassword().equals(password))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Error: Invalid email or password! Please try again."));
    }

}
