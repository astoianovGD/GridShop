package com.bobocode.services.orders;

import com.bobocode.dto.orders.OrderDto;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.entities.bucket.BucketItem;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.users.User;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.orders.OrderItemMapper;
import com.bobocode.mappers.orders.OrderMapper;
import com.bobocode.repositories.bucket.BucketItemRepository;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.orders.OrderRepository;
import com.bobocode.repositories.users.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private BucketRepository bucketRepository;
    @Mock private BucketItemRepository bucketItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Bucket testBucket;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setActive(true);

        testBucket = new Bucket();
        testBucket.setId(1L);
        testBucket.setUser(testUser);
        testBucket.setItems(new ArrayList<>());
    }

    @Test
    void shouldDoNothingWhenBucketIsNullOnCreateOrder() {
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.empty());

        orderService.createOrderFromBucket(1L);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldDoNothingWhenBucketIsEmptyOnCreateOrder() {
        testBucket.setItems(Collections.emptyList());
        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));

        orderService.createOrderFromBucket(1L);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundWhenUserNotFoundOnCreateOrder() {
        BucketItem bucketItem = new BucketItem();
        testBucket.getItems().add(bucketItem);

        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrderFromBucket(1L));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldCreateOrderFromBucketSuccessfully() {
        BucketItem bucketItem = new BucketItem();
        testBucket.getItems().add(bucketItem);

        when(bucketRepository.findByUserId(1L)).thenReturn(Optional.of(testBucket));
        when(userRepository.findUserByIdAndRoleNameAndIsActive(1L, "USER", true)).thenReturn(Optional.of(testUser));
        when(orderItemMapper.toOrderItem(eq(bucketItem), any(Order.class))).thenReturn(new OrderItem());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrderFromBucket(1L);

        verify(orderRepository).save(any(Order.class));
        verify(bucketRepository).save(testBucket);
        assertTrue(testBucket.getItems().isEmpty());
    }

    @Test
    void shouldGetUserOrdersSuccessfully() {
        Order order = new Order();
        OrderDto orderDto = new OrderDto();
        orderDto.setId(10L);

        when(orderRepository.findAllByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderDto);

        List<OrderDto> result = orderService.getUserOrders(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        verify(orderRepository).findAllByUserId(1L);
    }
}