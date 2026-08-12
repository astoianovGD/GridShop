package com.bobocode.entities.bucket;

import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
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
@Transactional // Rollback after every test, no need for deleteAll
public class BucketIntegrationTest {

    @Autowired private EntityManager entityManager;
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
        testUser.setEmail("test@test.com");
        testUser.setPassword("pass");
        testUser.setFirstname("Alex");
        testUser.setLastname("Stoianov");
        testUser.setRole(role);
        userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Laptop");
        testProduct.setPrice(new BigDecimal("1000.00"));
        testProduct.setCategory(category);
        productRepository.save(testProduct);
    }

    @Test
    void shouldSaveBucketAndItemsCascading() {
        Bucket bucket = new Bucket();
        bucket.setUser(testUser);
        bucket.setItems(new ArrayList<>());

        BucketItem item = new BucketItem();
        item.setBucket(bucket);
        item.setProduct(testProduct);
        item.setQuantity(1);
        bucket.getItems().add(item);

        bucketRepository.save(bucket);

        entityManager.flush();
        entityManager.clear();

        Bucket foundBucket = bucketRepository.findById(bucket.getId()).orElseThrow();
        assertEquals(1, foundBucket.getItems().size());
        assertEquals("Laptop", foundBucket.getItems().get(0).getProduct().getName());
    }

    @Test
    void shouldRemoveOrphanItemsWhenRemovedFromList() {
        // 1. Create and save bucket with item
        Bucket bucket = new Bucket();
        bucket.setUser(testUser);
        bucket.setItems(new ArrayList<>());

        BucketItem item = new BucketItem();
        item.setBucket(bucket);
        item.setProduct(testProduct);
        item.setQuantity(1);
        bucket.getItems().add(item);

        bucketRepository.save(bucket);
        entityManager.flush();
        entityManager.clear();

        // 2. Remove item from list
        Bucket managedBucket = bucketRepository.findById(bucket.getId()).orElseThrow();
        managedBucket.getItems().clear();
        bucketRepository.save(managedBucket); // orphanRemoval triggers DELETE

        entityManager.flush();
        entityManager.clear();

        // 3. Verify
        Bucket emptyBucket = bucketRepository.findById(bucket.getId()).orElseThrow();
        assertTrue(emptyBucket.getItems().isEmpty());
    }
}