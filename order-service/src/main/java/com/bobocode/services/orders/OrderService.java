package com.bobocode.services.orders;

import com.bobocode.clients.product.ProductClient;
import com.bobocode.clients.user.UserClient;
import com.bobocode.dto.products.ProductDto;
import com.bobocode.dto.orders.OrderDto;
import com.bobocode.entities.orders.Order;
import com.bobocode.entities.orders.OrderItem;
import com.bobocode.entities.bucket.Bucket;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.mappers.orders.OrderMapper;
import com.bobocode.repositories.bucket.BucketRepository;
import com.bobocode.repositories.orders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing orders and purchase history in order-service.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrderService {

    /**
     * Repository for order entities.
     */
    private final OrderRepository orderRepository;

    /**
     * Repository for bucket entities.
     */
    private final BucketRepository bucketRepository;

    /**
     * Mapper for orders.
     */
    private final OrderMapper orderMapper;

    /**
     * Client interface for user-service communication.
     */
    private final UserClient userClient;

    /**
     * Client interface for product-service communication.
     */
    private final ProductClient productClient;

    /**
     * Transfers products from the user's bucket to the order history table.
     *
     * @param userId the ID of the user making the purchase
     */
    @Transactional
    public void createOrderFromBucket(final Long userId) {
        // Validate user existence via user-service Feign client
        userClient.getUserById(userId);

        Bucket bucket = bucketRepository.findByUserId(userId).orElse(null);

        if (bucket == null || bucket.getItems().isEmpty()) {
            return;
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setPurchaseDate(LocalDateTime.now());

        List<OrderItem> orderItems = bucket.getItems().stream()
                .map(bucketItem -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProductId(bucketItem.getProductId());
                    item.setQuantity(bucketItem.getQuantity());
                    ProductDto product = productClient.getProductById(bucketItem.getProductId());
                    item.setPriceAtPurchase(product != null ? product.getPrice() : BigDecimal.ZERO);
                    item.setProductName(product != null ? product.getName() : "Product #" + bucketItem.getProductId());
                    return item;
                })
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
    public List<OrderDto> getUserOrders(final Long userId) {
        // Validate user existence via user-service Feign client
        userClient.getUserById(userId);

        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a specific order by its ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return the requested order as a DTO
     * @throws EntityNotFoundException if the order is not found
     */
    public OrderDto getOrderById(final Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order with ID " + orderId + " not found!"
                ));
    }
}
