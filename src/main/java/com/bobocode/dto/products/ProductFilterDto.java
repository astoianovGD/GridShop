package com.bobocode.dto.products;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for filtering and searching products.
 *
 * @param name        keyword for case-insensitive search by product name (LIKE)
 * @param minPrice    minimum price filter (GREATER_THAN)
 * @param maxPrice    maximum price filter (LESS_THAN)
 * @param categoryIds list of category IDs to filter by (IN)
 */
public record ProductFilterDto(
        String name,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        List<Long> categoryIds
) {
}
