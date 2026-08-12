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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderItemRepositoryIntegrationTest {

    @Autowired private EntityManager entityManager;
    @Autowired private OrderItemRepository orderItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private RoleRepository roleRepository;

    @Test
    void shouldSaveAndFindOrderItem() {
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
        user.setEmail("itemrepo@test.com");
        user.setPassword("pass");
        user.setFirstname("Alex");
        user.setLastname("Stoianov");
        user.setActive(true);
        user.setRole(role);
        userRepository.save(user);

        Product product = new Product();
        product.setName("Gaming Mouse");
        product.setPrice(new BigDecimal("75.00"));
        product.setCategory(category);
        product.setActive(true);
        productRepository.save(product);

        Order order = new Order();
        order.setUser(user);
        order.setPurchaseDate(LocalDateTime.now());
        orderRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setPriceAtPurchase(new BigDecimal("75.00"));
        item.setQuantity(3);
        orderItemRepository.save(item);

        entityManager.flush();
        entityManager.clear();

        Optional<OrderItem> foundItem = orderItemRepository.findById(item.getOrderItemId());

        assertTrue(foundItem.isPresent());
        assertEquals(3, foundItem.get().getQuantity());
        assertEquals(0, foundItem.get().getPriceAtPurchase().compareTo(new BigDecimal("75.00")));
        assertEquals(product.getId(), foundItem.get().getProduct().getId());
        assertEquals(order.getId(), foundItem.get().getOrder().getId());
    }
}