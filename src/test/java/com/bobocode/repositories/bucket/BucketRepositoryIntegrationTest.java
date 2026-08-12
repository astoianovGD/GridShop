package com.bobocode.repositories.bucket;

import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
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
public class BucketRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private BucketRepository bucketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @Test
    void shouldFindByUserId() {
        // Arrange
        Role role = roleRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("USER");
            return roleRepository.save(r);
        });

        User user = new User();
        user.setEmail("bucket.owner@test.com");
        user.setPassword("securepass");
        user.setFirstname("Alex");
        user.setLastname("Stoianov");
        user.setActive(true);
        user.setRole(role);
        userRepository.save(user);

        Bucket bucket = new Bucket();
        bucket.setUser(user);
        bucketRepository.save(bucket);

        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Bucket> foundBucket = bucketRepository.findByUserId(user.getId());

        // Assert
        assertTrue(foundBucket.isPresent());
        assertEquals(bucket.getId(), foundBucket.get().getId());
        assertEquals(user.getId(), foundBucket.get().getUser().getId());
    }
}