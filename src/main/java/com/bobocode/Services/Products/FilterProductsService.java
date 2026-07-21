package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for filtering products based on various criteria.
 */
@RequiredArgsConstructor
public final class FilterProductsService {

    /**
     * Filters products whose name starts with the given letter.
     *
     * @param letter      the starting letter to filter by
     * @param productList the list of products to filter
     * @return a list of filtered products
     */
    public List<Product> filterProductsByLetterBeginWith(
            final char letter, final List<Product> productList) {
        String searchPrefix = String.valueOf(letter).toLowerCase();
        return productList.stream()
                .filter(x -> x.getName().toLowerCase()
                        .startsWith(searchPrefix))
                .toList();
    }

    /**
     * Filters products with a price higher than or equal to the given price.
     *
     * @param price       the minimum price threshold
     * @param productList the list of products to filter
     * @return a list of filtered products
     */
    public List<Product> filterProductsByPriceHigherThan(
            final BigDecimal price, final List<Product> productList) {
        return productList.stream()
                .filter(x -> x.getPrice().compareTo(price) >= 0)
                .toList();
    }

    /**
     * Filters products with a price lower than or equal to the given price.
     *
     * @param price       the maximum price threshold
     * @param productList the list of products to filter
     * @return a list of filtered products
     */
    public List<Product> filterProductsByPriceLowerThan(
            final BigDecimal price, final List<Product> productList) {
        return productList.stream()
                .filter(x -> x.getPrice().compareTo(price) <= 0)
                .toList();
    }

    /**
     * Filters products whose name contains the given keyword.
     *
     * @param keyword     the keyword to search for in product names
     * @param productList the list of products to filter
     * @return a list of filtered products
     */
    public List<Product> filterProductsByName(
            final String keyword, final List<Product> productList) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return productList;
        }

        String lowerCaseKeyword = keyword.toLowerCase();

        return productList.stream()
                .filter(x -> x.getName().toLowerCase()
                        .contains(lowerCaseKeyword))
                .toList();
    }
}
