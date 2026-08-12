package com.bobocode.repositories.products;

import com.bobocode.entities.products.Category;
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
public class CategoryRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    void shouldFindByName() {
        Category category = new Category();
        category.setName("Peripherals");
        categoryRepository.save(category);

        Optional<Category> found = categoryRepository.findByName("Peripherals");

        assertTrue(found.isPresent());
        assertEquals("Peripherals", found.get().getName());
    }

    @Test
    void shouldCheckExistsByName() {
        Category category = new Category();
        category.setName("Monitors");
        categoryRepository.save(category);

        boolean exists = categoryRepository.existsByName("Monitors");
        boolean notExists = categoryRepository.existsByName("Unknown");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldCheckExistsById() {
        Category category = new Category();
        category.setName("Storage");
        Category saved = categoryRepository.save(category);

        boolean exists = categoryRepository.existsById(saved.getId());
        boolean notExists = categoryRepository.existsById(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void shouldGetNameById() {
        Category category = new Category();
        category.setName("Networking");
        Category saved = categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        Optional<String> name = categoryRepository.getNameById(saved.getId());

        assertTrue(name.isPresent());
        assertEquals("Networking", name.get());
    }
}