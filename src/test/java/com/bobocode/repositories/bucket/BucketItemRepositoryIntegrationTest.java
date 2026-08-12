package com.bobocode.repositories.bucket;

import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BucketItemRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private BucketItemRepository bucketItemRepository;
    @Autowired private BucketRepository bucketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RoleRepository roleRepository;

    private User testUser;
    private Product testProduct;
    private Bucket testBucket;

    @BeforeEach
    void setUp() {
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
        testUser.setEmail("buckettest@test.com");
        testUser.setPassword("pass");
        testUser.setFirstname("Alex");
        testUser.setLastname("Stoianov");
        testUser.setActive(true);
        testUser.setRole(role);
        userRepository.save(testUser);

        testBucket = new Bucket();
        testBucket.setUser(testUser);
        bucketRepository.save(testBucket);

        testProduct = new Product();
        testProduct.setName("Smartphone");
        testProduct.setPrice(new BigDecimal("500.00"));
        testProduct.setCategory(category);
        testProduct.setActive(true);
        productRepository.save(testProduct);
    }

    @Test
    void shouldFindByBucketIdAndProductId() {
        BucketItem item = new BucketItem();
        item.setBucket(testBucket);
        item.setProduct(testProduct);
        item.setQuantity(2);
        bucketItemRepository.save(item);

        Optional<BucketItem> found = bucketItemRepository.findByBucketIdAndProductId(testBucket.getId(), testProduct.getId());

        assertTrue(found.isPresent());
        assertEquals(2, found.get().getQuantity());
    }

    @Test
    void shouldDeleteByBucketIdAndProductId() {
        BucketItem item = new BucketItem();
        item.setBucket(testBucket);
        item.setProduct(testProduct);
        item.setQuantity(1);
        bucketItemRepository.save(item);

        bucketItemRepository.deleteByBucketIdAndProductId(testBucket.getId(), testProduct.getId());
        entityManager.flush();

        Optional<BucketItem> found = bucketItemRepository.findByBucketIdAndProductId(testBucket.getId(), testProduct.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindActiveUsersByActiveProductIdInBucket() {
        BucketItem item = new BucketItem();
        item.setBucket(testBucket);
        item.setProduct(testProduct);
        item.setQuantity(1);
        bucketItemRepository.save(item);

        List<User> activeUsers = bucketItemRepository.findActiveUsersByActiveProductIdInBucket(testProduct.getId());

        assertEquals(1, activeUsers.size());
        assertEquals(testUser.getId(), activeUsers.get(0).getId());
    }

    @Test
    void shouldDeleteAllByProductId() {
        BucketItem item = new BucketItem();
        item.setBucket(testBucket);
        item.setProduct(testProduct);
        item.setQuantity(1);
        bucketItemRepository.save(item);

        bucketItemRepository.deleteAllByProductId(testProduct.getId());
        entityManager.flush();
        entityManager.clear();

        List<BucketItem> items = bucketItemRepository.findAll();
        assertTrue(items.isEmpty());
    }
}