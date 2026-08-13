package com.bobocode.repositories.products;

import com.bobocode.entities.products.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing {@link Product} entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds active products whose name starts with the given string,
     * ignoring case.
     *
     * @param name     the prefix string
     * @param isActive the active status
     * @return a list of matching products
     */
    List<Product> findByNameStartingWithIgnoreCaseAndIsActive(
            String name, boolean isActive
    );

    /**
     * Finds active products with a price greater than the specified amount.
     *
     * @param price    the price threshold
     * @param isActive the active status
     * @return a list of matching products
     */
    List<Product> findByPriceGreaterThanAndIsActive(
            BigDecimal price, boolean isActive
    );

    /**
     * Finds active products with a price lower than the specified amount.
     *
     * @param price    the price threshold
     * @param isActive the active status
     * @return a list of matching products
     */
    List<Product> findByPriceLessThanAndIsActive(
            BigDecimal price, boolean isActive
    );

    /**
     * Finds active products whose name contains the given string,
     * ignoring case.
     *
     * @param name     the search keyword
     * @param isActive the active status
     * @return a list of matching products
     */
    List<Product> findByNameContainingIgnoreCaseAndIsActive(
            String name, boolean isActive
    );

    /**
     * Finds all active products with sorting applied.
     *
     * @param isActive the active status
     * @param sort     the sorting criteria
     * @return a sorted list of active products
     */
    List<Product> findAllByIsActive(boolean isActive, Sort sort);

    /**
     * Finds all products matching the specified active status.
     *
     * @param isActive the active status
     * @return a list of products
     */
    List<Product> findAllByIsActive(boolean isActive);

    /**
     * Finds a product by its active status and ID.
     *
     * @param isActive the active status
     * @param id       the product ID
     * @return an optional containing the product if found
     */
    Optional<Product> findProductByIsActiveAndId(boolean isActive, long id);

    /**
     * Checks if any products exist for a given category ID.
     *
     * @param categoryId the category ID
     * @return true if products exist, false otherwise
     */
    boolean existsByCategoryId(long categoryId);
}
