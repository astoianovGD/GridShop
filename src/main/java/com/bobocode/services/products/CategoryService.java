package com.bobocode.services.products;

import com.bobocode.entities.products.Category;
import com.bobocode.utility.CustomJdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing categories.
 */
@RequiredArgsConstructor
@Service
public class CategoryService {

    /** The JDBC template for database operations. */
    @NonNull
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Add new category.
     *
     * @param category the name of the category
     */
    public void addNewCategory(final String category) {
        if (isCategoryNameExists(category)) {
            throw new IllegalArgumentException(
                    "Category with name '" + category + "' already exists!"
            );
        }

        String sql = "INSERT INTO categories (name) VALUES (?)";
        customJdbcTemplate.execute(sql, category);
    }

    /**
     * Changing existing category.
     *
     * @param category the new name of the category
     * @param id       the ID of the category to update
     */
    public void editCategory(final String category, final long id) {
        if (isCategoryNameExists(category)) {
            throw new IllegalArgumentException(
                    "Category with name '" + category + "' already exists!"
            );
        }

        String sql = "UPDATE categories SET name = ? WHERE category_id = ?";
        customJdbcTemplate.execute(sql, category, id);
    }

    /**
     * Removes category safely.
     *
     * @param categoryId the ID of the category to remove
     */
    public void removeCategory(final long categoryId) {
        String checkSql =
                "SELECT EXISTS (SELECT 1 FROM products WHERE category_id = ?)";
        Boolean hasProducts = customJdbcTemplate.findOne(checkSql, rs -> {
            try {
                return rs.getBoolean(1);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, categoryId);

        if (hasProducts != null && hasProducts) {
            throw new IllegalStateException(
                    "Cannot delete category: it still contains products. "
                            + "Reassign or delete them first."
            );
        }

        String sql = "DELETE FROM categories WHERE category_id = ?";
        customJdbcTemplate.execute(sql, categoryId);
    }

    /**
     * Helper method to check if category name is already taken.
     *
     * @param name the name of the category to check
     * @return true if the category name exists, false otherwise
     */
    private boolean isCategoryNameExists(final String name) {
        String sql = "SELECT EXISTS "
                + "(SELECT 1 FROM categories WHERE LOWER(name) = LOWER(?))";
        Boolean exists = customJdbcTemplate.findOne(sql, rs -> {
            try {
                return rs.getBoolean(1);
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error checking category name existence", e
                );
            }
        }, name);
        return exists != null && exists;
    }

    /**
     * Retrieves a list of all categories.
     *
     * @return a list containing all categories
     */
    public List<Category> getAllCategories() {
        String sql = "SELECT category_id, name FROM categories";

        return customJdbcTemplate.findMany(sql, rs -> {
            try {
                Category category = new Category();
                category.setId(rs.getLong("category_id"));
                category.setName(rs.getString("name"));
                return category;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Category from ResultSet", e
                );
            }
        });
    }

    /**
     * Checks if a category exists by its ID.
     *
     * @param categoryId the ID to check
     * @return true if exists, false otherwise
     */
    public boolean isCategoryExists(final long categoryId) {
        String sql = "SELECT EXISTS "
                + "(SELECT 1 FROM categories WHERE category_id = ?)";
        Boolean exists = customJdbcTemplate.findOne(sql, rs -> {
            try {
                return rs.getBoolean(1);
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error checking category existence", e
                );
            }
        }, categoryId);
        return exists != null && exists;
    }
}
