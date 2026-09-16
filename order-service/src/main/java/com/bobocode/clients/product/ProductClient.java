package com.bobocode.clients.product;

import com.bobocode.dto.products.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communicating with product-service.
 */
@FeignClient(name = "product-service")
public interface ProductClient {

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id product ID
     * @return the product DTO
     */
    @GetMapping("/api/v1/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}

