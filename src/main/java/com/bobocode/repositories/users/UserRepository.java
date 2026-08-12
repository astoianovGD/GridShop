package com.bobocode.repositories.users;

import com.bobocode.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByRoleNameAndIsActive(String role_name, boolean isActive);

    boolean existsByEmail(String email);

    Optional<User> findUserByIdAndRoleNameAndIsActive(long id, String role_name, boolean isActive);

    Optional<User> findByEmail(String email);

    boolean existsByRoleName(String role_name);
}