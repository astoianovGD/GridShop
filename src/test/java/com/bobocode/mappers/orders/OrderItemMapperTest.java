package com.bobocode.mappers.orders;

import com.bobocode.dto.orders.OrderItemDto;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.products.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemMapperTest {

    private final OrderItemMapper mapper = Mappers.getMapper(OrderItemMapper.class);

    @Test
    void shouldMapBucketItemAndOrderToOrderItem() {
        // Arrange
        Product product = new Product();
        product.setId(10L);
        product.setName("Gaming Keyboard");
        product.setPrice(new BigDecimal("99.99"));

        BucketItem bucketItem = new BucketItem();
        bucketItem.setBucketItemId(5L);
        bucketItem.setProduct(product);
        bucketItem.setQuantity(2);

        Order order = new Order();
        order.setId(1L);

        // Act
        OrderItem orderItem = mapper.toOrderItem(bucketItem, order);

        // Assert
        assertNotNull(orderItem);
        assertEquals(0L, orderItem.getOrderItemId()); // ignored in mapper
        assertEquals(order, orderItem.getOrder());
        assertEquals(product, orderItem.getProduct());
        assertEquals(0, orderItem.getPriceAtPurchase().compareTo(new BigDecimal("99.99")));
        assertEquals(2, orderItem.getQuantity());
    }

    @Test
    void shouldMapOrderItemToDto() {
        // Arrange
        Product product = new Product();
        product.setId(20L);
        product.setName("Gaming Mouse");

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderItemId(3L);
        orderItem.setProduct(product);
        orderItem.setPriceAtPurchase(new BigDecimal("49.99"));
        orderItem.setQuantity(1);

        // Act
        OrderItemDto dto = mapper.toDto(orderItem);

        // Assert
        assertNotNull(dto);
        assertEquals(20L, dto.getProductId());
        assertEquals("Gaming Mouse", dto.getProductName());
    }
}