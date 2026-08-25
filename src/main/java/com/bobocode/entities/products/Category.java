package com.bobocode.entities.products;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product category.
 */
@Data
@Entity
@Table(name = "categories")
public class Category {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /**
     * The name of the category.
     */
    @Column(name = "name", unique = true, nullable = false)
    @NotNull
    @NotBlank
    @Size(max = 50)
    private String name;

    /**
     * The list of products belonging to this category.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Product> products = new ArrayList<>();
}
