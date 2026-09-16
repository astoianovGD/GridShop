package com.bobocode.services.orders;

import com.bobocode.clients.product.ProductClient;
import com.bobocode.clients.user.UserClient;
import com.bobocode.dto.orders.OrderDto;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.users.UserDto;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.orders.Order;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.orders.OrderMapper;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.orders.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private BucketRepository bucketRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserClient userClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    private Bucket testBucket;
    private UserDto testUserDto;
    private ProductDto testProductDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setEmail("user@example.com");

        testProductDto = new ProductDto();
        testProductDto.setId(10L);
        testProductDto.setName("Test Product");
        testProductDto.setPrice(BigDecimal.valueOf(99.99));

        testBucket = new Bucket();
        testBucket.setId(100L);
        testBucket.setUserId(1L);
        testBucket.setItems(new ArrayList<>());
    }

    @Test
    @DisplayName("createOrderFromBucket should validate user and do nothing when bucket is not found")
    void shouldDoNothingWhenBucketNotFoundOnCreateOrder() {
        when(userClient.getUserById(1L)).thenReturn(testUserDto);
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.empty());

        orderService.createOrderFromBucket(1L);

        verify(userClient).getUserById(1L);
        verify(orderRepository, never()).save(any());
        verify(productClient, never()).getProductById(any());
    }

    @Test
    @DisplayName("createOrderFromBucket should validate user and do nothing when bucket items list is empty")
    void shouldDoNothingWhenBucketIsEmptyOnCreateOrder() {
        testBucket.setItems(Collections.emptyList());
        when(userClient.getUserById(1L)).thenReturn(testUserDto);
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));

        orderService.createOrderFromBucket(1L);

        verify(userClient).getUserById(1L);
        verify(orderRepository, never()).save(any());
        verify(productClient, never()).getProductById(any());
    }

    @Test
    @DisplayName("createOrderFromBucket should fetch product details via Feign and save order successfully")
    void shouldCreateOrderFromBucketSuccessfully() {
        BucketItem item = new BucketItem();
        item.setBucketItemId(5L);
        item.setBucket(testBucket);
        item.setProductId(10L);
        item.setQuantity(2);
        testBucket.getItems().add(item);

        when(userClient.getUserById(1L)).thenReturn(testUserDto);
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(productClient.getProductById(10L)).thenReturn(testProductDto);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrderFromBucket(1L);

        verify(userClient).getUserById(1L);
        verify(productClient).getProductById(10L);
        verify(orderRepository).save(any(Order.class));
        verify(bucketRepository).save(testBucket);
        assertTrue(testBucket.getItems().isEmpty());
    }

    @Test
    @DisplayName("getUserOrders should validate user via Feign and return mapped orders")
    void shouldGetUserOrdersSuccessfully() {
        Order order = new Order();
        OrderDto orderDto = new OrderDto();
        orderDto.setId(50L);

        when(userClient.getUserById(1L)).thenReturn(testUserDto);
        when(orderRepository.findAllByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getUserOrders(1L);

        assertEquals(1, result.size());
        assertEquals(50L, result.get(0).getId());
        verify(userClient).getUserById(1L);
        verify(orderRepository).findAllByUserId(1L);
    }

    @Test
    @DisplayName("getOrderById should return OrderDto when order exists")
    void shouldGetOrderByIdSuccessfully() {
        Order order = new Order();
        OrderDto orderDto = new OrderDto();
        orderDto.setId(50L);

        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        OrderDto result = orderService.getOrderById(50L);

        assertNotNull(result);
        assertEquals(50L, result.getId());
        verify(orderRepository).findById(50L);
    }

    @Test
    @DisplayName("getOrderById should throw EntityNotFoundException when order does not exist")
    void shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(999L));
        verify(orderRepository).findById(999L);
    }
}
