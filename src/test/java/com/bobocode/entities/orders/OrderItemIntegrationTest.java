package com.bobocode.entities.orders;

import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.repositories.orders.OrderItemRepository;
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
public class OrderItemIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private OrderItemRepository orderItemRepository;
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
        testUser.setEmail("orderitemtest@test.com");
        testUser.setPassword("pass");
        testUser.setFirstname("Alex");
        testUser.setLastname("Stoianov");
        testUser.setRole(role);
        userRepository.save(testUser);

        testProduct = new Product();
        testProduct.setName("Mechanical Keyboard");
        testProduct.setPrice(new BigDecimal("120.00"));
        testProduct.setCategory(category);
        productRepository.save(testProduct);
    }

    @Test
    void shouldSaveAndRetrieveOrderItem() {
        Order order = new Order();
        order.setUser(testUser);
        order.setPurchaseDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());
        orderRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(testProduct);
        item.setPriceAtPurchase(new BigDecimal("120.00"));
        item.setQuantity(2);


        orderItemRepository.save(item);

        entityManager.flush();
        entityManager.clear();

        OrderItem foundItem = orderItemRepository.findById(item.getOrderItemId()).orElseThrow();

        assertNotNull(foundItem.getOrderItemId());
        assertEquals(2, foundItem.getQuantity());
        assertEquals(0, foundItem.getPriceAtPurchase().compareTo(new BigDecimal("120.00")));
        assertEquals(testProduct.getId(), foundItem.getProduct().getId());
        assertEquals(order.getId(), foundItem.getOrder().getId());
    }
}