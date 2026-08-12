package com.bobocode.mappers.orders;

import com.bobocode.dto.orders.OrderDto;
import com.bobocode.dto.orders.OrderItemDto;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.products.Product;
import com.bobocode.entities.users.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class OrderMapperTest {

    @Autowired
    private OrderMapper orderMapper;

    @Test
    void shouldMapOrderToDtoWithItems() {
        // Arrange
        User user = new User();
        user.setId(5L);

        Product product = new Product();
        product.setId(100L);
        product.setName("Monitor");

        OrderItem item = new OrderItem();
        item.setOrderItemId(1L);
        item.setProduct(product);
        item.setPriceAtPurchase(new BigDecimal("299.99"));
        item.setQuantity(1);

        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();
        order.setId(10L);
        order.setUser(user);
        order.setPurchaseDate(now);
        order.setItems(new ArrayList<>());
        order.getItems().add(item);

        // Act
        OrderDto dto = orderMapper.toDto(order);

        // Assert
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals(now, dto.getPurchaseDate());
        assertNotNull(dto.getItems());
        assertEquals(1, dto.getItems().size());

        OrderItemDto itemDto = dto.getItems().get(0);
        assertEquals(100L, itemDto.getProductId());
        assertEquals("Monitor", itemDto.getProductName());
    }
}