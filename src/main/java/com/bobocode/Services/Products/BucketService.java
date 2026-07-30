package com.bobocode.Services.Products;

import com.bobocode.Entities.Products.Product;
import com.bobocode.Utility.JdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing user buckets.
 */
@RequiredArgsConstructor
public final class BucketService {

    /** The JDBC template for database operations. */
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    /**
     * Adds a product to the specified user's bucket.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to be added
     * @param amount    the amount of the product to add
     */
    public void addProductToBucket(
            final long userId, final long productId, final int amount
    ) {
        Long bucketId = getOrCreateBucketId(userId);

        String sql = "INSERT INTO bucket_items "
                + "(bucket_id, product_id, quantity) "
                + "VALUES (?, ?, ?) "
                + "ON CONFLICT (bucket_id, product_id) "
                + "DO UPDATE SET quantity = "
                + "bucket_items.quantity + EXCLUDED.quantity";

        jdbcTemplate.execute(sql, bucketId, productId, amount);
    }

    /**
     * Removes a product from the specified user's bucket.
     *
     * @param userId    the ID of the user
     * @param productId the ID of the product to be removed
     */
    public void removeProductFromBucket(
            final long userId, final long productId
    ) {
        Long bucketId = getBucketIdByUserId(userId);
        if (bucketId == null) {
            return;
        }

        String sql = "DELETE FROM bucket_items "
                + "WHERE bucket_id = ? AND product_id = ?";
        jdbcTemplate.execute(sql, bucketId, productId);
    }

    /**
     * Retrieves the list of products from the specified user's bucket.
     *
     * @param userId the ID of the user
     * @return a list of products currently in the bucket
     */
    public List<Product> getProductsFromBucket(final long userId) {
        Long bucketId = getBucketIdByUserId(userId);
        if (bucketId == null) {
            return List.of();
        }

        String sql = "SELECT p.product_id, p.name, p.price, "
                + "p.category_id, bi.quantity "
                + "FROM products p "
                + "JOIN bucket_items bi ON p.product_id = bi.product_id "
                + "WHERE bi.bucket_id = ? AND p.is_active = true";

        return jdbcTemplate.findMany(sql, rs -> {
            try {
                Product product = new Product();
                product.setId(rs.getLong("product_id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategoryId(rs.getLong("category_id"));

                product.setQuantity(rs.getInt("quantity"));

                return product;
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Product from ResultSet", e
                );
            }
        }, bucketId);
    }

    /**
     * Helper method to get bucket_id by user_id.
     *
     * @param userId the ID of the user
     * @return the bucket ID, or null if not found
     */
    private Long getBucketIdByUserId(final long userId) {
        String sql = "SELECT bucket_id FROM bucket WHERE user_id = ?";
        return jdbcTemplate.findOne(sql, rs -> {
            try {
                return rs.getLong("bucket_id");
            } catch (SQLException e) {
                throw new RuntimeException("Error getting bucket id", e);
            }
        }, userId);
    }

    /**
     * Helper method to get bucket_id, or create a new one if it doesn't exist.
     *
     * @param userId the ID of the user
     * @return the bucket ID
     */
    private Long getOrCreateBucketId(final long userId) {
        Long bucketId = getBucketIdByUserId(userId);
        if (bucketId != null) {
            return bucketId;
        }

        String insertSql = "INSERT INTO bucket (user_id) VALUES (?) "
                + "ON CONFLICT (user_id) DO NOTHING";
        jdbcTemplate.execute(insertSql, userId);

        return getBucketIdByUserId(userId);
    }

    /**
     * Clears all items from the specified user's bucket.
     *
     * @param userId the ID of the user
     */
    public void clearBucket(final long userId) {
        Long bucketId = getBucketIdByUserId(userId);
        if (bucketId != null) {
            String sql = "DELETE FROM bucket_items WHERE bucket_id = ?";
            jdbcTemplate.execute(sql, bucketId);
        }
    }
}
