package com.bobocode.services.orders;

import com.bobocode.dto.orders.OrderDto;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.orders.OrderItemMapper;
import com.bobocode.mappers.orders.OrderMapper;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.orders.OrderRepository;
import com.bobocode.repositories.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing orders and purchase history.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class    OrderService {

    /**
     * Repository for order entities.
     */
    private final OrderRepository orderRepository;

    /**
     * Repository for bucket entities.
     */
    private final BucketRepository bucketRepository;

    /**
     * Repository for user entities.
     */
    private final UserRepository userRepository;

    /**
     * Mapper for order items.
     */
    private final OrderItemMapper orderItemMapper;

    /**
     * Mapper for orders.
     */
    private final OrderMapper orderMapper;

    /**
     * Transfers products from the user's bucket to the order history table.
     *
     * @param userId the ID of the user making the purchase
     */
    @Transactional
    public void createOrderFromBucket(final long userId) {
        Bucket bucket = bucketRepository.findByUserId(userId).orElse(null);

        if (bucket == null || bucket.getItems().isEmpty()) {
            return;
        }

        Order order = new Order();

        var user = userRepository
                .findUserByIdAndRoleNameAndIsActive(userId, "USER", true)
                .orElseThrow(() -> new EntityNotFoundException(
                        "User with ID " + userId + " not found!"
                ));

        order.setUser(user);
        order.setPurchaseDate(LocalDateTime.now());

        List<OrderItem> orderItems = bucket.getItems().stream()
                .map(bucketItem -> orderItemMapper.toOrderItem(
                        bucketItem, order
                ))
                .toList();

        order.getItems().addAll(orderItems);

        orderRepository.save(order);

        bucket.getItems().clear();

        bucketRepository.save(bucket);
    }

    /**
     * Retrieves the list of all orders DTOs for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of user's orders DTOs
     */
    @Transactional
    public List<OrderDto> getUserOrders(final long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }


    public OrderDto getOrderById(long id) {
        return orderMapper.toDto(orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No Order with id - " + id)));
    }
}
