package com.bobocode.repositories.users;

import com.bobocode.entities.users.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private RoleRepository roleRepository;

    @Test
    void shouldFindByName() {
        Role role = new Role();

        role.setId(999L);
        role.setName("TEST_ROLE_CUSTOM");
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName("TEST_ROLE_CUSTOM");

        assertTrue(found.isPresent());
        assertEquals("TEST_ROLE_CUSTOM", found.get().getName());
    }
}