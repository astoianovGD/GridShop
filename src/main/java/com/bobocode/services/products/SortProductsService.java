package com.bobocode.services.products;

import com.bobocode.entities.products.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Service responsible for sorting product lists.
 */
@RequiredArgsConstructor
@Service
public final class SortProductsService {

    /**
     * Sorts a list of products by price in ascending order.
     *
     * @param productList the list of products to sort
     * @return a sorted list of products
     */
    public List<Product> sortProductsByPriceAsc(
            final List<Product> productList) {
        return productList.stream()
                .sorted(Comparator.comparing(Product::getPrice))
                .toList();
    }

    /**
     * Sorts a list of products by price in descending order.
     *
     * @param productList the list of products to sort
     * @return a sorted list of products
     */
    public List<Product> sortProductsByPriceDesc(
            final List<Product> productList) {
        return productList.stream()
                .sorted(Comparator.comparing(Product::getPrice).reversed())
                .toList();
    }

    /**
     * Sorts a list of products by name in ascending order.
     *
     * @param productList the list of products to sort
     * @return a sorted list of products
     */
    public List<Product> sortProductsByNameAsc(
            final List<Product> productList) {
        return productList.stream()
                .sorted(Comparator.comparing(Product::getName))
                .toList();
    }

    /**
     * Sorts a list of products by name in descending order.
     *
     * @param productList the list of products to sort
     * @return a sorted list of products
     */
    public List<Product> sortProductsByNameDesc(
            final List<Product> productList) {
        return productList.stream()
                .sorted(Comparator.comparing(Product::getName).reversed())
                .toList();
    }
}
