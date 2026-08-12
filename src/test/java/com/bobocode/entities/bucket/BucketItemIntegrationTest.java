package com.bobocode.entities.bucket;


import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
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
public class BucketItemIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private BucketItemRepository bucketItemRepository;
    @Autowired private BucketRepository bucketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RoleRepository roleRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setup() {
        Role role = roleRepository.findByName("USER").orElseGet(() -> {
            Role r = new Role();
            r.setName("USER");
            return roleRepository.save(r);
        });

        Category category = categoryRepository.findByName("Test Cat").orElseGet(() -> {
            Category c = new Category();
            c.setName("Test Cat");
            return categoryRepository.save(c);
        });

        testUser = new User();
        testUser.setEmail("itemtest@test.com");
        testUser.setPassword("pass");
        testUser.setFirstname("Alex");
        testUser.setLastname("Stoianov");
        testUser.setRole(role);
        userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Smartphone");
        testProduct.setPrice(new BigDecimal("500.00"));
        testProduct.setCategory(category);
        productRepository.save(testProduct);
    }

    @Test
    void shouldSaveAndRetrieveBucketItem() {
        Bucket bucket = new Bucket();
        bucket.setUser(testUser);
        bucket.setItems(new ArrayList<>());
        bucketRepository.save(bucket);

        BucketItem item = new BucketItem();
        item.setBucket(bucket);
        item.setProduct(testProduct);
        item.setQuantity(3);

        bucketItemRepository.save(item);

        entityManager.flush();
        entityManager.clear();

        BucketItem foundItem = bucketItemRepository.findById(item.getBucketItemId()).orElseThrow();

        assertNotNull(foundItem.getBucketItemId());
        assertEquals(3, foundItem.getQuantity());
        assertEquals(testProduct.getId(), foundItem.getProduct().getId());
        assertEquals(bucket.getId(), foundItem.getBucket().getId());
    }
}