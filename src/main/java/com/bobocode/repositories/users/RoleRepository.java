package com.bobocode.repositories.users;

import com.bobocode.entities.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link Role} entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its name.
     *
     * @param name the role name
     * @return an optional containing the role if found
     */
    Optional<Role> findByName(String name);
}
