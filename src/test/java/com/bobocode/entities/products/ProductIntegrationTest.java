package com.bobocode.entities.products;

import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    void shouldSaveAndRetrieveProduct() {
        Category category = new Category();
        category.setName("Home Appliances");
        categoryRepository.save(category);

        Product product = new Product();
        product.setName("Blender");
        product.setPrice(new BigDecimal("49.99"));
        product.setCategory(category);
        product.setActive(true);

        productRepository.save(product);

        entityManager.flush();
        entityManager.clear();

        Product foundProduct = productRepository.findById(product.getId()).orElseThrow();

        assertNotNull(foundProduct.getId());
        assertEquals("Blender", foundProduct.getName());
        assertEquals(0, foundProduct.getPrice().compareTo(new BigDecimal("49.99")));
        assertTrue(foundProduct.isActive());
        assertEquals(category.getId(), foundProduct.getCategory().getId());
    }
}