package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Utility.JdbcTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BucketServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BucketService bucketService;

    @Test
    void addProductToBucket_WhenBucketExists_ShouldExecuteInsert() {
        long userId = 1L;
        long productId = 100L;
        int amount = 2;
        long bucketId = 5L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(bucketId);

        bucketService.addProductToBucket(userId, productId, amount);

        verify(jdbcTemplate, times(1)).execute(anyString(), eq(bucketId), eq(productId), eq(amount));
    }

    @Test
    void addProductToBucket_WhenBucketNotExists_ShouldCreateBucketAndExecuteInsert() {
        long userId = 1L;
        long productId = 100L;
        int amount = 1;
        long bucketId = 5L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId)))
                .thenReturn(null)
                .thenReturn(bucketId);

        bucketService.addProductToBucket(userId, productId, amount);

        verify(jdbcTemplate, times(1)).execute(eq("INSERT INTO bucket (user_id) VALUES (?) ON CONFLICT (user_id) DO NOTHING"), eq(userId));
        verify(jdbcTemplate, times(1)).execute(anyString(), eq(bucketId), eq(productId), eq(amount));
    }

    @Test
    void removeProductFromBucket_WhenBucketExists_ShouldExecuteDelete() {
        long userId = 1L;
        long productId = 100L;
        long bucketId = 5L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(bucketId);

        bucketService.removeProductFromBucket(userId, productId);

        verify(jdbcTemplate, times(1)).execute(anyString(), eq(bucketId), eq(productId));
    }

    @Test
    void removeProductFromBucket_WhenBucketNotExists_ShouldDoNothing() {
        long userId = 1L;
        long productId = 100L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(null);

        bucketService.removeProductFromBucket(userId, productId);

        verify(jdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }

    @Test
    void getProductsFromBucket_WhenBucketExists_ShouldReturnProducts() throws SQLException {
        long userId = 1L;
        long bucketId = 5L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(bucketId);

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getLong("product_id")).thenReturn(10L);
        when(resultSet.getString("name")).thenReturn("Laptop");
        when(resultSet.getBigDecimal("price")).thenReturn(new BigDecimal("1000.00"));
        when(resultSet.getLong("category_id")).thenReturn(2L);
        when(resultSet.getInt("quantity")).thenReturn(1);

        when(jdbcTemplate.findMany(anyString(), any(Function.class), eq(bucketId))).thenAnswer(invocation -> {
            Function<ResultSet, Product> mapper = invocation.getArgument(1);
            return List.of(mapper.apply(resultSet));
        });

        List<Product> products = bucketService.getProductsFromBucket(userId);

        assertEquals(1, products.size());
        assertEquals("Laptop", products.get(0).getName());
    }

    @Test
    void getProductsFromBucket_WhenBucketNotExists_ShouldReturnEmptyList() {
        long userId = 1L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(null);

        List<Product> products = bucketService.getProductsFromBucket(userId);

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    @Test
    void clearBucket_WhenBucketExists_ShouldExecuteDelete() {
        long userId = 1L;
        long bucketId = 5L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(bucketId);

        bucketService.clearBucket(userId);

        verify(jdbcTemplate, times(1)).execute(anyString(), eq(bucketId));
    }

    @Test
    void clearBucket_WhenBucketNotExists_ShouldDoNothing() {
        long userId = 1L;

        when(jdbcTemplate.findOne(anyString(), any(Function.class), eq(userId))).thenReturn(null);

        bucketService.clearBucket(userId);

        verify(jdbcTemplate, never()).execute(anyString(), (Object[]) any());
    }
}