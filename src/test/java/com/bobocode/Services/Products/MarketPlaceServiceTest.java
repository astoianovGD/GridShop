package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Utility.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketPlaceServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private MarketPlaceService marketPlaceService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setCategoryId(2L);
    }

    @Test
    void addNewProduct_ShouldExecuteInsert() {
        marketPlaceService.addNewProduct(testProduct);

        verify(jdbcTemplate, times(1)).execute(
                anyString(),
                eq(testProduct.getName()),
                eq(testProduct.getPrice()),
                eq(testProduct.getCategoryId())
        );
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        when(jdbcTemplate.findOne(anyString(), any(), eq(1L))).thenReturn(testProduct);

        Product result = marketPlaceService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProductById_WhenNotExists_ShouldThrowException() {
        when(jdbcTemplate.findOne(anyString(), any(), eq(99L))).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> marketPlaceService.getProductById(99L)
        );
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void removeProduct_ShouldDeactivateAndRemoveFromBucket() {
        // Mock getProductById to avoid exception
        when(jdbcTemplate.findOne(anyString(), any(), eq(1L))).thenReturn(testProduct);

        marketPlaceService.removeProduct(1L);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, times(2)).execute(sqlCaptor.capture(), eq(1L));

        List<String> executedQueries = sqlCaptor.getAllValues();
        assertTrue(executedQueries.get(0).contains("UPDATE products SET is_active = false"));
        assertTrue(executedQueries.get(1).contains("DELETE FROM bucket_items"));
    }
}