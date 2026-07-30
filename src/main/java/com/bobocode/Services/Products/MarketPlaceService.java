package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Category;
import com.bobocode.Entities.Products.Product;
import com.bobocode.Exceptions.EntityNotFoundException;
import com.bobocode.Utility.JdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing products within the marketplace.
 */
@RequiredArgsConstructor
public final class MarketPlaceService {

    /** The JDBC template for database operations. */
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    /**
     * Adds a new product to the marketplace.
     *
     * @param product the product to be added
     */
    public void addNewProduct(final Product product) {
        String sql = "INSERT INTO products (name, price, category_id) "
                + "VALUES (?, ?, ?)";
        jdbcTemplate.execute(sql,
                product.getName(),
                product.getPrice(),
                product.getCategoryId()
        );
    }

    /**
     * Removes a product from the marketplace by its ID.
     *
     * @param id the ID of the product to remove
     * @throws EntityNotFoundException if the product is not found
     */
    public void removeProduct(final long id) {
        getProductById(id);

        String sql = "UPDATE products SET is_active = false "
                + "WHERE product_id = ?";
        jdbcTemplate.execute(sql, id);

        String removeFromBucketsSql = "DELETE FROM bucket_items "
                + "WHERE product_id = ?";
        jdbcTemplate.execute(removeFromBucketsSql, id);
    }

    /**
     * Edits an existing product in the marketplace.
     *
     * @param product the product with updated information
     */
    public void editProduct(final Product product) {
        getProductById(product.getId());

        String sql = "UPDATE products SET name = ?, price = ?, "
                + "category_id = ? WHERE product_id = ?";
        jdbcTemplate.execute(sql,
                product.getName(),
                product.getPrice(),
                product.getCategoryId(),
                product.getId()
        );
    }

    /**
     * Retrieves a list of all products in the marketplace.
     *
     * @return a list containing all products
     */
    public List<Product> getAllProducts() {
        String sql = "SELECT product_id, name, price, category_id "
                + "FROM products WHERE is_active = true";

        return jdbcTemplate.findMany(sql, rs -> {
            try {
                Product product = new Product();
                product.setId(rs.getLong("product_id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategoryId(rs.getLong("category_id"));
                return product;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Product from ResultSet", e
                );
            }
        });
    }

    /**
     * Retrieves a product from the marketplace by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return the requested product
     * @throws EntityNotFoundException if the product is not found
     */
    public Product getProductById(final long id) {
        String sql = "SELECT product_id, name, price, category_id "
                + "FROM products WHERE product_id = ? AND is_active = true";

        Product product = jdbcTemplate.findOne(sql, rs -> {
            try {
                Product p = new Product();
                p.setId(rs.getLong("product_id"));
                p.setName(rs.getString("name"));
                p.setPrice(rs.getBigDecimal("price"));
                p.setCategoryId(rs.getLong("category_id"));
                return p;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Product from ResultSet", e
                );
            }
        }, id);

        if (product == null) {
            throw new EntityNotFoundException(
                    "Product with ID " + id + " not found!"
            );
        }

        return product;
    }

    /**
     * Retrieves a list of all categories.
     *
     * @return a list containing all categories
     */
    public List<Category> getAllCategories() {
        String sql = "SELECT category_id, name FROM categories";

        return jdbcTemplate.findMany(sql, rs -> {
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
        Boolean exists = jdbcTemplate.findOne(sql, rs -> {
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
