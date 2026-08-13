package com.bobocode.repositories.users;

import com.bobocode.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link User} entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds all active users matching a given role name.
     *
     * @param roleName the role name
     * @param isActive the active status
     * @return a list of matching users
     */
    List<User> findAllByRoleNameAndIsActive(
            String roleName, boolean isActive
    );

    /**
     * Checks if a user exists with the specified email.
     *
     * @param email the email address
     * @return true if a user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Finds an active user by ID and role name.
     *
     * @param id       the user ID
     * @param roleName the role name
     * @param isActive the active status
     * @return an optional containing the user if found
     */
    Optional<User> findUserByIdAndRoleNameAndIsActive(
            long id, String roleName, boolean isActive
    );

    /**
     * Finds a user by email address.
     *
     * @param email the email address
     * @return an optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if any users exist with the specified role name.
     *
     * @param roleName the role name
     * @return true if users exist, false otherwise
     */
    boolean existsByRoleName(String roleName);
}
