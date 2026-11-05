package com.bobocode.security;

import com.bobocode.repositories.orders.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("orderSecurity")
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public boolean isOrderOwner(Long orderId, Long currentUserId) {
        if (currentUserId == null) {
            return false;
        }
        return orderRepository.findById(orderId)
                .map(order -> order.getUserId().equals(currentUserId))
                .orElse(true);
    }
}
