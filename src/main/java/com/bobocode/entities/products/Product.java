package com.bobocode.entities.products;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Long id;

    /**
     * The name of the product.
     */
    @Column(name = "name", nullable = false)
    @NotBlank
    @NotNull
    @Size(max = 50)
    private String name;

    /**
     * The price of the product.
     */
    @Column(name = "price", nullable = false)
    @NotNull
    @Min(0)
    private BigDecimal price;

    /**
     * The category associated with the product.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull
    private Category category;

    /**
     * Indicates whether the product is active.
     */
    @Column(name = "is_active")
    @NotNull
    private boolean isActive;
}
