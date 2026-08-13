package com.bobocode.repositories.products;

import com.bobocode.entities.products.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing {@link Category} entities.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its name.
     *
     * @param name the category name
     * @return an optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Checks if a category exists by its name.
     *
     * @param name the category name
     * @return true if the category exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Checks if a category exists by its ID.
     *
     * @param id the category ID
     * @return true if the category exists, false otherwise
     */
    boolean existsById(long id);

    /**
     * Retrieves a category name by its ID.
     *
     * @param id the category ID
     * @return an optional containing the category name if found
     */
    @Query("SELECT c.name FROM Category c WHERE c.id = :id")
    Optional<String> getNameById(@Param("id") long id);
}
