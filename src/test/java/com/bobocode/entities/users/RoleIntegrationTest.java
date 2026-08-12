package com.bobocode.entities.users;

import com.bobocode.repositories.users.RoleRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RoleIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private RoleRepository roleRepository;

    @Test
    void shouldSaveRoleAndUsersCascading() {
        Role role = new Role();
        role.setId(10L);
        role.setName("MANAGER");
        role.setUsers(new ArrayList<>());

        User user = new User();
        user.setEmail("manager@test.com");
        user.setPassword("pass");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setRole(role);
        role.getUsers().add(user);

        roleRepository.save(role);

        entityManager.flush();
        entityManager.clear();

        Role foundRole = roleRepository.findById(role.getId()).orElseThrow();
        assertEquals("MANAGER", foundRole.getName());
        assertEquals(1, foundRole.getUsers().size());
        assertEquals("manager@test.com", foundRole.getUsers().get(0).getEmail());
    }
}