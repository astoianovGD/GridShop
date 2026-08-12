package com.bobocode.entities.users;

import com.bobocode.enums.Gender;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @Test
    void shouldSaveAndRetrieveUser() {
        Role role = new Role();
        role.setId(20L);
        role.setName("STAFF");
        roleRepository.save(role);

        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("securepass");
        user.setFirstname("Alice");
        user.setLastname("Smith");
        user.setAge(25);
        user.setGender(Gender.FEMALE);
        user.setActive(true);
        user.setRole(role);

        userRepository.save(user);

        entityManager.flush();
        entityManager.clear();

        User foundUser = userRepository.findById(user.getId()).orElseThrow();

        assertNotNull(foundUser.getId());
        assertEquals("user@test.com", foundUser.getEmail());
        assertEquals("Alice", foundUser.getFirstname());
        assertEquals("Smith", foundUser.getLastname());
        assertEquals(25, foundUser.getAge());
        assertEquals(Gender.FEMALE, foundUser.getGender());
        assertTrue(foundUser.isActive());
        assertEquals(role.getId(), foundUser.getRole().getId());
    }
}