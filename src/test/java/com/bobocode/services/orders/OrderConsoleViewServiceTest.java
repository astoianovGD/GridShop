package com.bobocode.services.orders;

import com.bobocode.dto.orders.OrderDto;
import com.bobocode.dto.orders.OrderItemDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class OrderConsoleViewServiceTest {

    private final OrderConsoleViewService orderConsoleViewService = new OrderConsoleViewService();

    @Test
    void shouldDisplayEmptyHistoryWhenListIsNull() {
        assertDoesNotThrow(() -> orderConsoleViewService.displayOrderHistory(null));
    }

    @Test
    void shouldDisplayEmptyHistoryWhenListIsEmpty() {
        assertDoesNotThrow(() -> orderConsoleViewService.displayOrderHistory(Collections.emptyList()));
    }

    @Test
    void shouldDisplayOrderHistorySuccessfully() {
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setProductName("Laptop");
        itemDto.setPriceAtPurchase(new BigDecimal("1200.00"));
        itemDto.setQuantity(1);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(1L);
        orderDto.setPurchaseDate(LocalDateTime.now());
        orderDto.setItems(List.of(itemDto));

        List<OrderDto> orders = List.of(orderDto);

        assertDoesNotThrow(() -> orderConsoleViewService.displayOrderHistory(orders));
    }

    @Test
    void shouldDisplayOrderHistoryWhenItemsAreNull() {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(2L);
        orderDto.setPurchaseDate(LocalDateTime.now());
        orderDto.setItems(null);

        List<OrderDto> orders = List.of(orderDto);

        assertDoesNotThrow(() -> orderConsoleViewService.displayOrderHistory(orders));
    }
}