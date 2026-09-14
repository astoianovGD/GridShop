package com.bobocode.services.system;

import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling user authentication.
 */
@RequiredArgsConstructor
@Service
public class AuthService {

    /**
     * Repository for managing user entities.
     */
    private final UserRepository userRepository;

    /**
     * Signs a user in using their email and password.
     *
     * @param email    the user's email
     * @param password the user's password
     * @return the authenticated user entity
     * @throws EntityNotFoundException if credentials are invalid
     */
    @Transactional(readOnly = true)
    public User signIn(final String email, final String password) {
        System.out.println("Searching in base...");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Error: Invalid email or password! Please try again."
                ));

        if (!user.getPassword().equals(password)) {
            throw new EntityNotFoundException(
                    "Error: Invalid email or password! Please try again."
            );
        }

        System.out.println("Successfully logged in as: "
                + user.getRole().getName());
        return user;
    }
}
