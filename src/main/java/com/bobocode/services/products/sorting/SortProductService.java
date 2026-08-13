package com.bobocode.services.products.sorting;

import com.bobocode.dto.products.ProductDto;
import com.bobocode.mappers.products.ProductMapper;
import com.bobocode.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for sorting products by price or name.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SortProductService {

    /**
     * Repository for managing products.
     */
    private final ProductRepository productRepository;

    /**
     * Mapper for products.
     */
    private final ProductMapper productMapper;

    /**
     * Sorts active products by price in ascending order.
     *
     * @return a sorted list of product DTOs
     */
    public List<ProductDto> filterByPriceAsc() {
        return productRepository
                .findAllByIsActive(true, Sort.by(Sort.Direction.ASC, "price"))
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Sorts active products by price in descending order.
     *
     * @return a sorted list of product DTOs
     */
    public List<ProductDto> filterByProductDesc() {
        return productRepository
                .findAllByIsActive(true, Sort.by(Sort.Direction.DESC, "price"))
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Sorts active products by name in ascending order.
     *
     * @return a sorted list of product DTOs
     */
    public List<ProductDto> filterByNameAsc() {
        return productRepository
                .findAllByIsActive(true, Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    /**
     * Sorts active products by name in descending order.
     *
     * @return a sorted list of product DTOs
     */
    public List<ProductDto> filterByNameDesc() {
        return productRepository
                .findAllByIsActive(true, Sort.by(Sort.Direction.DESC, "name"))
                .stream()
                .map(productMapper::toDto)
                .toList();
    }
}
