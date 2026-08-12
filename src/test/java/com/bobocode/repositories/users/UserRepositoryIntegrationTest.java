package com.bobocode.repositories.users;

import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("USER");
            return roleRepository.save(r);
        });
    }

    @Test
    void shouldFindAllByRoleNameAndIsActive() {
        User user = new User();
        user.setEmail("role.active@test.com");
        user.setPassword("pass");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setActive(true);
        user.setRole(testRole);
        userRepository.save(user);

        List<User> users = userRepository.findAllByRoleNameAndIsActive("USER", true);

        assertFalse(users.isEmpty());
        assertEquals("John", users.get(0).getFirstname());
    }

    @Test
    void shouldCheckExistsByEmail() {
        User user = new User();
        user.setEmail("exists@test.com");
        user.setPassword("pass");
        user.setFirstname("Jane");
        user.setLastname("Doe");
        user.setActive(true);
        user.setRole(testRole);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@test.com");
        boolean notExists = userRepository.existsByEmail("wrong@test.com");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldFindUserByIdAndRoleNameAndIsActive() {
        User user = new User();
        user.setEmail("id.role@test.com");
        user.setPassword("pass");
        user.setFirstname("Bob");
        user.setLastname("Builder");
        user.setActive(true);
        user.setRole(testRole);
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findUserByIdAndRoleNameAndIsActive(saved.getId(), "USER", true);

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstname());
    }

    @Test
    void shouldFindByEmail() {
        User user = new User();
        user.setEmail("find.email@test.com");
        user.setPassword("pass");
        user.setFirstname("Alice");
        user.setLastname("Smith");
        user.setActive(true);
        user.setRole(testRole);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("find.email@test.com");

        assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getFirstname());
    }

    @Test
    void shouldCheckExistsByRoleName() {
        User user = new User();
        user.setEmail("role.check@test.com");
        user.setPassword("pass");
        user.setFirstname("Charlie");
        user.setLastname("Brown");
        user.setActive(true);
        user.setRole(testRole);
        userRepository.save(user);

        boolean exists = userRepository.existsByRoleName("USER");
        boolean notExists = userRepository.existsByRoleName("SUPERADMIN");

        assertTrue(exists);
        assertFalse(notExists);
    }
}