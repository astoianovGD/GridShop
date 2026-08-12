package com.bobocode.services.products;

import com.bobocode.dto.products.ProductDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ProductsConsoleViewServiceTest {

    private final ProductsConsoleViewService productsConsoleViewService = new ProductsConsoleViewService();

    @Test
    void shouldDisplayEmptyMarketplaceWhenListIsNull() {
        assertDoesNotThrow(() -> productsConsoleViewService.catalogAllProducts(null));
    }

    @Test
    void shouldDisplayEmptyMarketplaceWhenListIsEmpty() {
        assertDoesNotThrow(() -> productsConsoleViewService.catalogAllProducts(Collections.emptyList()));
    }

    @Test
    void shouldCatalogAllProductsSuccessfully() {
        ProductDto product1 = new ProductDto();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setPrice(new BigDecimal("1200.00"));

        ProductDto product2 = new ProductDto();
        product2.setId(2L);
        product2.setName("Smartphone");
        product2.setPrice(new BigDecimal("800.50"));

        List<ProductDto> products = List.of(product1, product2);

        assertDoesNotThrow(() -> productsConsoleViewService.catalogAllProducts(products));
    }
}