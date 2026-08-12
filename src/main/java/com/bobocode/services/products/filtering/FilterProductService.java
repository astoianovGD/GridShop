package com.bobocode.services.products.filtering;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilterProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public List<ProductDto> filterByNameStartingWith(String startWith) {
        return productRepository
                .findByNameStartingWithIgnoreCaseAndIsActive(startWith, true)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    public List<ProductDto> filterByPriceGreaterThan(BigDecimal minPriceAndHigher) {
        return productRepository
                .findByPriceGreaterThanAndIsActive(minPriceAndHigher, true)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    public List<ProductDto> filterByPriceLowerThan(BigDecimal maxPriceAndLower) {
        return productRepository
                .findByPriceLessThanAndIsActive(maxPriceAndLower, true)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    public List<ProductDto> filterByNameContains(String keyword) {
        return productRepository
                .findByNameContainingIgnoreCaseAndIsActive(keyword, true)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }
}
