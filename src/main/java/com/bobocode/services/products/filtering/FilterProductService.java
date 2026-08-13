package com.bobocode.services.products.filtering;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for filtering products based on various criteria.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FilterProductService {

    /**
     * Repository for managing products.
     */
    private final ProductRepository productRepository;

    /**
     * Mapper for products.
     */
    private final ProductMapper productMapper;

    /**
     * Filters active products whose names start with the specified prefix.
     *
     * @param startWith the prefix string
     * @return a list of matching product DTOs
     */
    public List<ProductDto> filterByNameStartingWith(
            final String startWith
    ) {
        return productRepository
                .findByNameStartingWithIgnoreCaseAndIsActive(
                        startWith, true
                )
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Filters active products with a price greater than the minimum.
     *
     * @param minPriceAndHigher the minimum price threshold
     * @return a list of matching product DTOs
     */
    public List<ProductDto> filterByPriceGreaterThan(
            final BigDecimal minPriceAndHigher
    ) {
        return productRepository
                .findByPriceGreaterThanAndIsActive(
                        minPriceAndHigher, true
                )
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Filters active products with a price lower than the maximum.
     *
     * @param maxPriceAndLower the maximum price threshold
     * @return a list of matching product DTOs
     */
    public List<ProductDto> filterByPriceLowerThan(
            final BigDecimal maxPriceAndLower
    ) {
        return productRepository
                .findByPriceLessThanAndIsActive(
                        maxPriceAndLower, true
                )
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Filters active products whose names contain the keyword.
     *
     * @param keyword the search keyword
     * @return a list of matching product DTOs
     */
    public List<ProductDto> filterByNameContains(
            final String keyword
    ) {
        return productRepository
                .findByNameContainingIgnoreCaseAndIsActive(
                        keyword, true
                )
                .stream()
                .map(productMapper::toDto)
                .toList();
    }
}
