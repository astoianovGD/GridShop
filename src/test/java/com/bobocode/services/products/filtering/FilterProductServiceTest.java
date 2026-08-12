package com.bobocode.services.products.filtering;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.products.Product;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.repositories.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FilterProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private FilterProductService filterProductService;

    @Test
    void shouldFilterByNameStartingWithSuccessfully() {
        Product product = new Product();
        product.setName("Laptop");
        ProductDto dto = new ProductDto();
        dto.setName("Laptop");

        when(productRepository.findByNameStartingWithIgnoreCaseAndIsActive("Lap", true))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = filterProductService.filterByNameStartingWith("Lap");

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository).findByNameStartingWithIgnoreCaseAndIsActive("Lap", true);
    }

    @Test
    void shouldReturnEmptyListWhenNoProductStartsWithName() {
        when(productRepository.findByNameStartingWithIgnoreCaseAndIsActive("X", true))
                .thenReturn(Collections.emptyList());

        List<ProductDto> result = filterProductService.filterByNameStartingWith("X");

        assertTrue(result.isEmpty());
        verify(productRepository).findByNameStartingWithIgnoreCaseAndIsActive("X", true);
    }

    @Test
    void shouldFilterByPriceGreaterThanSuccessfully() {
        BigDecimal minPrice = new BigDecimal("100.00");
        Product product = new Product();
        product.setPrice(new BigDecimal("150.00"));
        ProductDto dto = new ProductDto();

        when(productRepository.findByPriceGreaterThanAndIsActive(minPrice, true))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = filterProductService.filterByPriceGreaterThan(minPrice);

        assertEquals(1, result.size());
        verify(productRepository).findByPriceGreaterThanAndIsActive(minPrice, true);
    }

    @Test
    void shouldFilterByPriceLowerThanSuccessfully() {
        BigDecimal maxPrice = new BigDecimal("50.00");
        Product product = new Product();
        product.setPrice(new BigDecimal("25.00"));
        ProductDto dto = new ProductDto();

        when(productRepository.findByPriceLessThanAndIsActive(maxPrice, true))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = filterProductService.filterByPriceLowerThan(maxPrice);

        assertEquals(1, result.size());
        verify(productRepository).findByPriceLessThanAndIsActive(maxPrice, true);
    }

    @Test
    void shouldFilterByNameContainsSuccessfully() {
        String keyword = "phone";
        Product product = new Product();
        product.setName("Smartphone");
        ProductDto dto = new ProductDto();
        dto.setName("Smartphone");

        when(productRepository.findByNameContainingIgnoreCaseAndIsActive(keyword, true))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = filterProductService.filterByNameContains(keyword);

        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getName());
        verify(productRepository).findByNameContainingIgnoreCaseAndIsActive(keyword, true);
    }
}