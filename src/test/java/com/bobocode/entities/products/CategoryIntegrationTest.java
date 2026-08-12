package com.bobocode.entities.products;

import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CategoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;

    @Test
    void shouldSaveCategoryAndProductsCascading() {
        Category category = new Category();
        category.setName("Electronics");
        category.setProducts(new ArrayList<>());

        Product product = new Product();
        product.setName("Smart TV");
        product.setPrice(new BigDecimal("799.99"));
        product.setCategory(category);
        category.getProducts().add(product);

        categoryRepository.save(category);

        entityManager.flush();
        entityManager.clear();

        Category foundCategory = categoryRepository.findById(category.getId()).orElseThrow();
        assertEquals(1, foundCategory.getProducts().size());
        assertEquals("Smart TV", foundCategory.getProducts().get(0).getName());
    }

    @Test
    void shouldRemoveOrphanProductsWhenRemovedFromList() {
        Category category = new Category();
        category.setName("Books");
        category.setProducts(new ArrayList<>());

        Product product = new Product();
        product.setName("Java Programming");
        product.setPrice(new BigDecimal("45.00"));
        product.setCategory(category);
        category.getProducts().add(product);

        categoryRepository.save(category);
        entityManager.flush();
        entityManager.clear();

        Category managedCategory = categoryRepository.findById(category.getId()).orElseThrow();
        managedCategory.getProducts().clear();
        categoryRepository.save(managedCategory);

        entityManager.flush();
        entityManager.clear();

        Category emptyCategory = categoryRepository.findById(category.getId()).orElseThrow();
        assertTrue(emptyCategory.getProducts().isEmpty());
    }
}