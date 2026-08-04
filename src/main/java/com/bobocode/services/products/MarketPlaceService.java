package com.bobocode.services.products;

import com.bobocode.entities.products.Product;
import com.bobocode.exceptions.EntityNotFoundException;
import com.bobocode.utility.CustomJdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing products within the marketplace.
 */
@RequiredArgsConstructor
@Service
public final class MarketPlaceService {

    /** The JDBC template for database operations. */
    @NonNull
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Adds a new product to the marketplace.
     *
     * @param product the product to be added
     */
    public void addNewProduct(final Product product) {
        String sql = "INSERT INTO products (name, price, category_id) "
                + "VALUES (?, ?, ?)";
        customJdbcTemplate.execute(sql,
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
        customJdbcTemplate.execute(sql, id);

        String removeFromBucketsSql = "DELETE FROM bucket_items "
                + "WHERE product_id = ?";
        customJdbcTemplate.execute(removeFromBucketsSql, id);
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
        customJdbcTemplate.execute(sql,
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

        return customJdbcTemplate.findMany(sql, this::mapProductRow);
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

        Product product = customJdbcTemplate.findOne(
                sql, this::mapProductRow, id
        );

        if (product == null) {
            throw new EntityNotFoundException(
                    "Product with ID " + id + " not found!"
            );
        }

        return product;
    }

    /**
     * Maps a ResultSet row to a Product object.
     *
     * @param rs the result set row
     * @return the mapped product object
     */
    private Product mapProductRow(final ResultSet rs) {
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
    }
}
