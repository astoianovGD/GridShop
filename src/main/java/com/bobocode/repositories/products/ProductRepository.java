package com.bobocode.repositories.products;

import com.bobocode.entities.products.Product;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameStartingWithIgnoreCaseAndIsActive(String name, boolean isActive);

    List<Product> findByPriceGreaterThanAndIsActive(BigDecimal price, boolean isActive);

    List<Product> findByPriceLessThanAndIsActive(BigDecimal price, boolean isActive);

    List<Product> findByNameContainingIgnoreCaseAndIsActive(String name, boolean isActive);

    List<Product> findAllByIsActive(boolean isActive, Sort sort);

    List<Product> findAllByIsActive(boolean isActive);

    Optional<Product> findProductByIsActiveAndId(boolean isActive, long id);

    boolean existsByCategoryId(long categoryId);
}