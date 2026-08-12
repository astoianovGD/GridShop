package com.bobocode.repositories.products;

import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setName("Components");
        categoryRepository.save(testCategory);
    }

    @Test
    void shouldFindByNameStartingWithIgnoreCaseAndIsActive() {
        Product p1 = new Product();
        p1.setName("Graphics Card");
        p1.setPrice(new BigDecimal("800.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("Processor");
        p2.setPrice(new BigDecimal("300.00"));
        p2.setCategory(testCategory);
        p2.setActive(false);
        productRepository.save(p2);

        List<Product> products = productRepository.findByNameStartingWithIgnoreCaseAndIsActive("graph", true);

        assertEquals(1, products.size());
        assertEquals("Graphics Card", products.get(0).getName());
    }

    @Test
    void shouldFindByPriceGreaterThanAndIsActive() {
        Product p1 = new Product();
        p1.setName("SSD 1TB");
        p1.setPrice(new BigDecimal("100.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        List<Product> products = productRepository.findByPriceGreaterThanAndIsActive(new BigDecimal("50.00"), true);

        assertEquals(1, products.size());
        assertEquals("SSD 1TB", products.get(0).getName());
    }

    @Test
    void shouldFindByPriceLessThanAndIsActive() {
        Product p1 = new Product();
        p1.setName("RAM 16GB");
        p1.setPrice(new BigDecimal("60.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        List<Product> products = productRepository.findByPriceLessThanAndIsActive(new BigDecimal("100.00"), true);

        assertEquals(1, products.size());
        assertEquals("RAM 16GB", products.get(0).getName());
    }

    @Test
    void shouldFindByNameContainingIgnoreCaseAndIsActive() {
        Product p1 = new Product();
        p1.setName("Gaming Power Supply");
        p1.setPrice(new BigDecimal("150.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        List<Product> products = productRepository.findByNameContainingIgnoreCaseAndIsActive("power", true);

        assertEquals(1, products.size());
        assertEquals("Gaming Power Supply", products.get(0).getName());
    }

    @Test
    void shouldFindAllByIsActiveWithSort() {
        Product p1 = new Product();
        p1.setName("A Product");
        p1.setPrice(new BigDecimal("10.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        Product p2 = new Product();
        p2.setName("B Product");
        p2.setPrice(new BigDecimal("20.00"));
        p2.setCategory(testCategory);
        p2.setActive(true);
        productRepository.save(p2);

        List<Product> products = productRepository.findAllByIsActive(true, Sort.by(Sort.Direction.DESC, "name"));

        assertEquals(2, products.size());
        assertEquals("B Product", products.get(0).getName());
    }

    @Test
    void shouldFindAllByIsActive() {
        Product p1 = new Product();
        p1.setName("Cooler");
        p1.setPrice(new BigDecimal("45.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        List<Product> products = productRepository.findAllByIsActive(true);

        assertEquals(1, products.size());
    }

    @Test
    void shouldFindProductByIsActiveAndId() {
        Product p1 = new Product();
        p1.setName("Motherboard");
        p1.setPrice(new BigDecimal("200.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        Product saved = productRepository.save(p1);

        Optional<Product> found = productRepository.findProductByIsActiveAndId(true, saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Motherboard", found.get().getName());
    }

    @Test
    void shouldCheckExistsByCategoryId() {
        Product p1 = new Product();
        p1.setName("Case");
        p1.setPrice(new BigDecimal("90.00"));
        p1.setCategory(testCategory);
        p1.setActive(true);
        productRepository.save(p1);

        boolean exists = productRepository.existsByCategoryId(testCategory.getId());
        boolean notExists = productRepository.existsByCategoryId(999L);

        assertTrue(exists);
        assertFalse(notExists);
    }
}