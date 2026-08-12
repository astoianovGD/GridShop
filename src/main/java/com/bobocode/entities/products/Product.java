package com.bobocode.entities.products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a product in the marketplace.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    /**
     * Unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long id;

    /**
     * The name of the product.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The price of the product.
     */
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    /**
     * The category ID associated with the product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "is_active")
    private boolean isActive;

}
