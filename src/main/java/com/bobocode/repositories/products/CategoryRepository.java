package com.bobocode.repositories.products;

import com.bobocode.entities.products.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    boolean existsByName(String name);

    boolean existsById(long id);

    @Query("SELECT c.name FROM Category c WHERE c.id = :id")
    Optional<String> getNameById(@Param("id") long id);
}
