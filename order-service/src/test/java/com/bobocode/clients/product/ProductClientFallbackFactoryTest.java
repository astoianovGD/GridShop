package com.bobocode.clients.product;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.exceptions.EntityNotFoundException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ProductClientFallbackFactoryTest {

    private ProductClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new ProductClientFallbackFactory();
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException on 404")
    void shouldThrowEntityNotFoundExceptionOn404() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/v1/products/5",
                Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFound = new FeignException.NotFound("Not Found", request, null, null);

        ProductClient client = fallbackFactory.create(notFound);

        assertThrows(EntityNotFoundException.class, () -> client.getProductById(5L));
    }

    @Test
    @DisplayName("Should return fallback ProductDto on service failure")
    void shouldReturnFallbackProductDtoOnFailure() {
        RuntimeException failure = new RuntimeException("Read timed out");

        ProductClient client = fallbackFactory.create(failure);
        ProductDto result = client.getProductById(5L);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Product #5 (temporarily unavailable)", result.getName());
        assertEquals(BigDecimal.ZERO, result.getPrice());
        assertEquals(Boolean.FALSE, result.getIsActive());
    }
}
