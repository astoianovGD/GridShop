package com.bobocode.entities.orders;

import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.Role;
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
@Transactional
public class OrderIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private OrderRepository orderRepository;
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
        testUser.setEmail("ordertest@test.com");
        testUser.setPassword("pass");
        testUser.setFirstname("Alex");
        testUser.setLastname("Stoianov");
        testUser.setRole(role);
        userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Headphones");
        testProduct.setPrice(new BigDecimal("150.00"));
        testProduct.setCategory(category);
        productRepository.save(testProduct);
    }

    @Test
    void shouldSaveOrderAndItemsCascading() {
        Order order = new Order();
        order.setUser(testUser);
        order.setPurchaseDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(testProduct);
        item.setPriceAtPurchase(new BigDecimal("150.00"));
        item.setQuantity(2);
        order.getItems().add(item);

        orderRepository.save(order);

        entityManager.flush();
        entityManager.clear();

        Order foundOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(1, foundOrder.getItems().size());
        assertEquals("Headphones", foundOrder.getItems().get(0).getProduct().getName());
        assertEquals(2, foundOrder.getItems().get(0).getQuantity());
    }

    @Test
    void shouldRemoveOrphanOrderItemsWhenRemovedFromList() {
        Order order = new Order();
        order.setUser(testUser);
        order.setPurchaseDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(testProduct);
        item.setPriceAtPurchase(new BigDecimal("150.00"));
        item.setQuantity(1);
        order.getItems().add(item);

        orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        Order managedOrder = orderRepository.findById(order.getId()).orElseThrow();
        managedOrder.getItems().clear();
        orderRepository.save(managedOrder);

        entityManager.flush();
        entityManager.clear();

        Order emptyOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertTrue(emptyOrder.getItems().isEmpty());
    }
}