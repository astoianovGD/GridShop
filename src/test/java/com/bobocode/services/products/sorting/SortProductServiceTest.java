package com.bobocode.services.products.sorting;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.entities.products.Product;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.repositories.products.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SortProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private SortProductService sortProductService;

    @Test
    void shouldFilterByPriceAscSuccessfully() {
        Product product = new Product();
        product.setName("Cheap Product");
        ProductDto dto = new ProductDto();
        dto.setName("Cheap Product");

        Sort expectedSort = Sort.by(Sort.Direction.ASC, "price");
        when(productRepository.findAllByIsActive(eq(true), eq(expectedSort)))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = sortProductService.filterByPriceAsc();

        assertEquals(1, result.size());
        assertEquals("Cheap Product", result.get(0).getName());
        verify(productRepository).findAllByIsActive(true, expectedSort);
    }

    @Test
    void shouldFilterByProductDescSuccessfully() {
        Product product = new Product();
        product.setName("Expensive Product");
        ProductDto dto = new ProductDto();
        dto.setName("Expensive Product");

        Sort expectedSort = Sort.by(Sort.Direction.DESC, "price");
        when(productRepository.findAllByIsActive(eq(true), eq(expectedSort)))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = sortProductService.filterByProductDesc();

        assertEquals(1, result.size());
        assertEquals("Expensive Product", result.get(0).getName());
        verify(productRepository).findAllByIsActive(true, expectedSort);
    }

    @Test
    void shouldFilterByNameAscSuccessfully() {
        Product product = new Product();
        product.setName("A Product");
        ProductDto dto = new ProductDto();
        dto.setName("A Product");

        Sort expectedSort = Sort.by(Sort.Direction.ASC, "name");
        when(productRepository.findAllByIsActive(eq(true), eq(expectedSort)))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = sortProductService.filterByNameAsc();

        assertEquals(1, result.size());
        assertEquals("A Product", result.get(0).getName());
        verify(productRepository).findAllByIsActive(true, expectedSort);
    }

    @Test
    void shouldFilterByNameDescSuccessfully() {
        Product product = new Product();
        product.setName("Z Product");
        ProductDto dto = new ProductDto();
        dto.setName("Z Product");

        Sort expectedSort = Sort.by(Sort.Direction.DESC, "name");
        when(productRepository.findAllByIsActive(eq(true), eq(expectedSort)))
                .thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(dto);

        List<ProductDto> result = sortProductService.filterByNameDesc();

        assertEquals(1, result.size());
        assertEquals("Z Product", result.get(0).getName());
        verify(productRepository).findAllByIsActive(true, expectedSort);
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsFoundForSorting() {
        Sort expectedSort = Sort.by(Sort.Direction.ASC, "price");
        when(productRepository.findAllByIsActive(eq(true), eq(expectedSort)))
                .thenReturn(Collections.emptyList());

        List<ProductDto> result = sortProductService.filterByPriceAsc();

        assertTrue(result.isEmpty());
        verify(productRepository).findAllByIsActive(true, expectedSort);
    }
}