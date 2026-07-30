package com.bobocode.Services.Products;

import com.bobocode.Utility.JdbcTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrderFromBucket_WhenBucketExists_ShouldCreateOrderAndClearBucket() {
        long userId = 1L;
        long bucketId = 5L;
        long orderId = 10L;

        when(jdbcTemplate.findOne(contains("FROM bucket"), any(Function.class), eq(userId))).thenReturn(bucketId);
        when(jdbcTemplate.findOne(contains("INSERT INTO orders"), any(Function.class), eq(userId))).thenReturn(orderId);

        orderService.createOrderFromBucket(userId);

        verify(jdbcTemplate, times(1)).execute(
                contains("INSERT INTO order_items"),
                eq(orderId),
                eq(bucketId)
        );
        verify(jdbcTemplate, times(1)).execute(
                contains("DELETE FROM bucket_items"),
                eq(bucketId)
        );
    }

    @Test
    void createOrderFromBucket_WhenBucketIsNull_ShouldDoNothing() {
        long userId = 1L;
        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(null);

        orderService.createOrderFromBucket(userId);

        verify(jdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }

    @Test
    void getOrderHistory_ShouldReturnFormattedStrings() throws SQLException {
        long userId = 1L;

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("order_id")).thenReturn(1L);
        when(resultSet.getTimestamp("purchase_date")).thenReturn(Timestamp.valueOf("2023-10-10 12:00:00"));
        when(resultSet.getString("name")).thenReturn("Phone");
        when(resultSet.getBigDecimal("price_at_purchase")).thenReturn(new BigDecimal("500"));
        when(resultSet.getInt("quantity")).thenReturn(1);

        when(jdbcTemplate.findMany(anyString(), any(Function.class), eq(userId))).thenAnswer(invocation -> {
            Function<ResultSet, String> mapper = invocation.getArgument(1);
            return List.of(mapper.apply(resultSet));
        });

        List<String> actualHistory = orderService.getOrderHistory(userId);

        assertEquals(1, actualHistory.size());
        assertEquals("Order ID: 1 | Date: 2023-10-10 12:00:00.0 | Product: Phone | Price: $500 | Quantity: 1", actualHistory.get(0));
    }
}