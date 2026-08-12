package com.bobocode.repositories.orders;

import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.products.Category;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.Role;
import com.bobocode.entities.users.User;
import com.bobocode.repositories.products.CategoryRepository;
import com.bobocode.repositories.products.ProductRepository;
import com.bobocode.repositories.users.RoleRepository;
import com.bobocode.repositories.users.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RoleRepository roleRepository;

    @Test
    void shouldFindAllByUserIdWithFetchedItemsAndProducts() {
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

        User user = new User();
        user.setEmail("orderrepo@test.com");
        user.setPassword("pass");
        user.setFirstname("Alex");
        user.setLastname("Stoianov");
        user.setActive(true);
        user.setRole(role);
        userRepository.save(user);

        Product product = new Product();
        product.setName("Mechanical Keyboard");
        product.setPrice(new BigDecimal("120.00"));
        product.setCategory(category);
        product.setActive(true);
        productRepository.save(product);

        Order order = new Order();
        order.setUser(user);
        order.setPurchaseDate(LocalDateTime.now());
        order.setItems(new ArrayList<>());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setPriceAtPurchase(new BigDecimal("120.00"));
        item.setQuantity(1);
        order.getItems().add(item);

        orderRepository.save(order);

        entityManager.flush();
        entityManager.clear();

        List<Order> orders = orderRepository.findAllByUserId(user.getId());

        assertEquals(1, orders.size());
        Order foundOrder = orders.get(0);
        assertEquals(order.getId(), foundOrder.getId());
        assertEquals(1, foundOrder.getItems().size());
        assertEquals("Mechanical Keyboard", foundOrder.getItems().get(0).getProduct().getName());
    }
}