package com.bobocode;

import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.User;
import com.bobocode.repositories.orders.OrderRepository;
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
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JpaLifecycleTest {

    @Autowired private EntityManager entityManager;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RoleRepository roleRepository;

    private User testUser;
    private Category testCategory;

    @BeforeEach
    @Transactional
    void setup() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // Safe role retrieval or creation
        var userRole = roleRepository.findByName("USER").orElseGet(() -> {
            var role = new com.bobocode.entities.users.Role();
            role.setName("USER");
            return roleRepository.save(role);
        });

        // Safe category creation to avoid unique constraint violation
        testCategory = categoryRepository.findByName("Test Category").orElseGet(() -> {
            Category cat = new Category();
            cat.setName("Test Category");
            return categoryRepository.save(cat);
        });

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("pass");
        user.setFirstname("Alex");
        user.setLastname("Stoianov");
        user.setRole(userRole);
        testUser = userRepository.save(user);
    }

    @Test
    @Transactional
    void testLifecycleOperations() {
        Order order = new Order();
        order.setUser(testUser);
        order.setPurchaseDate(LocalDateTime.now());

        // 3. Save Parent without ID using repository.save(), entityManager.persist(), entityManager.merge()
        orderRepository.save(order);

        Order orderWithId = new Order();
        // 4. Save Parent with an initialized ID using repository.save(), entityManager.persist(), entityManager.merge()
        orderWithId.setId(999L);
        orderWithId.setUser(testUser);
        orderWithId.setPurchaseDate(LocalDateTime.now());
        orderRepository.save(orderWithId);

        Order duplicate = new Order();
        // 5. Insert Parent with some ID, save another Parent with the same ID using repository.save(), entityManager.persist(), entityManager.merge()
        duplicate.setId(999L);
    }

    @Test
    @Transactional
    void testRelationshipsImplementation() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setCategory(testCategory);
        productRepository.save(product);

        // 6. Save Parent with Children, which are not present in the database - using the same 3 approaches
        Order parent1 = new Order();
        parent1.setUser(testUser);
        parent1.setPurchaseDate(LocalDateTime.now());

        OrderItem child1 = new OrderItem();
        child1.setOrder(parent1);
        child1.setProduct(product);
        child1.setPriceAtPurchase(new BigDecimal("10.00"));
        child1.setQuantity(1);

        parent1.setItems(new ArrayList<>());
        parent1.getItems().add(child1);
        orderRepository.save(parent1);

        // 7. Save Parent with Children, which are already present in the database - using the same 3 approaches
        Order tempParent = new Order();
        tempParent.setUser(testUser);
        tempParent.setPurchaseDate(LocalDateTime.now());
        orderRepository.save(tempParent);

        OrderItem existingChild = new OrderItem();
        existingChild.setProduct(product);
        existingChild.setPriceAtPurchase(new BigDecimal("10.00"));
        existingChild.setQuantity(1);
        existingChild.setOrder(tempParent);

        entityManager.persist(existingChild);
        entityManager.flush();

        Order parent2 = new Order();
        parent2.setUser(testUser);
        parent2.setPurchaseDate(LocalDateTime.now());
        parent2.setItems(new ArrayList<>());
        parent2.getItems().add(existingChild);
        existingChild.setOrder(parent2);
        orderRepository.save(parent2);

        OrderItem orphan = new OrderItem();
        // 8. Save Child without Parent - using the same 3 approaches
        assertThrows(Exception.class, () -> {
            entityManager.persist(orphan);
            entityManager.flush();
        });

        // 9. Save Child with Parent initialized, but not present in the database - using the same 3 approaches
        Order newParent = new Order();
        OrderItem childWithNewParent = new OrderItem();
        childWithNewParent.setOrder(newParent);
        childWithNewParent.setProduct(product);

        Order orderForDetach = new Order();
        orderForDetach.setUser(testUser);
        orderForDetach.setPurchaseDate(LocalDateTime.now());
        Order managedParent = orderRepository.save(orderForDetach);

        entityManager.detach(managedParent);

        OrderItem childAttachedToDetached = new OrderItem();
        // 10. Save Child with Parent initialized, present in the database, but detached from EntityManager/Session - using the same 3 approaches
        childAttachedToDetached.setOrder(managedParent);
        childAttachedToDetached.setProduct(product);
        childAttachedToDetached.setPriceAtPurchase(new BigDecimal("10.00"));
        childAttachedToDetached.setQuantity(1);
    }

    @Test
    @Transactional
    void testDirtyChecking() {
        Order order = new Order();
        order.setUser(testUser);
        order.setPurchaseDate(LocalDateTime.now());
        orderRepository.save(order);
        entityManager.flush();

        // 11. Fetch the Parent with JpaRepository, try changing it and don't save it explicitly. Flush the session and check whether the changes were propagated to the database
        Order managed = orderRepository.findById(order.getId()).get();
        managed.setPurchaseDate(LocalDateTime.now().plusDays(1));
        entityManager.flush();
    }

    @Test
    void testNoTransaction() {
        Order order = new Order();
        order.setUser(testUser);
        order.setPurchaseDate(LocalDateTime.now());
        orderRepository.save(order);

        // 12. Start the transaction, fetch the Parent with JpaRepository, try changing it and don't save it explicitly. Flush the session and check whether the changes were propagated to the database
        Order detached = orderRepository.findById(order.getId()).get();
        detached.setPurchaseDate(LocalDateTime.now().plusDays(10));
    }
}