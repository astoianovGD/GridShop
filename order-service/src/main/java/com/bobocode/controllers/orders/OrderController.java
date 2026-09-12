package com.bobocode.controllers.orders;

import com.bobocode.dto.orders.OrderDto;
import com.bobocode.services.orders.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * Get all orders of user.
     * GET /api/v1/users/1/orders
     */
    @GetMapping("/users/{userId}/orders")
    public List<OrderDto> getAllOrdersByUserId(@PathVariable Long userId) {
        return orderService.getUserOrders(userId);
    }

    /**
     * Get order by id.
     * GET /api/v1/orders/5
     */
    @GetMapping("/orders/{id}")
    public OrderDto getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    /**
     * Create new order for user.
     * POST /api/v1/users/1/orders
     */
    @PostMapping("/users/{userId}/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@PathVariable Long userId) {
        orderService.createOrderFromBucket(userId);
    }
}