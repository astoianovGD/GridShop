package com.bobocode.clients.product;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.exceptions.EntityNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Fallback factory for ProductClient when product-service is unavailable or circuit breaker is open.
 */
@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(final Throwable cause) {
        return id -> {
            log.error("Fallback triggered for getProductById with ID {}. Cause: {}", id, cause.getMessage());
            if (cause instanceof FeignException.NotFound) {
                throw new EntityNotFoundException("Product with ID " + id + " not found!");
            }
            ProductDto fallbackProduct = new ProductDto();
            fallbackProduct.setId(id);
            fallbackProduct.setName("Product #" + id + " (temporarily unavailable)");
            fallbackProduct.setPrice(BigDecimal.ZERO);
            fallbackProduct.setIsActive(false);
            return fallbackProduct;
        };
    }
}
