package com.bobocode.services.products;

import com.bobocode.utility.CustomJdbcTemplate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Service for managing orders and purchase history.
 */
@RequiredArgsConstructor
@Service
public final class OrderService {

    /** The JDBC template for database operations. */
    @NonNull
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Transfers products from the user's bucket to the order history table.
     *
     * @param userId the ID of the user making the purchase
     */
    public void createOrderFromBucket(final long userId) {
        String getBucketIdSql =
                "SELECT bucket_id FROM bucket WHERE user_id = ?";
        Long bucketId = customJdbcTemplate.findOne(getBucketIdSql, rs -> {
            try {
                return rs.getLong("bucket_id");
            } catch (SQLException e) {
                throw new RuntimeException("Error getting bucket id", e);
            }
        }, userId);

        if (bucketId == null) {
            return;
        }

        String createOrderSql = "INSERT INTO orders (user_id, purchase_date) "
                + "VALUES (?, CURRENT_TIMESTAMP) RETURNING order_id";
        Long orderId = customJdbcTemplate.findOne(createOrderSql, rs -> {
            try {
                return rs.getLong("order_id");
            } catch (SQLException e) {
                throw new RuntimeException("Error creating order", e);
            }
        }, userId);

        String moveItemsSql = "INSERT INTO order_items "
                + "(order_id, product_id, price_at_purchase, quantity) "
                + "SELECT ?, bi.product_id, p.price, bi.quantity "
                + "FROM bucket_items bi "
                + "JOIN products p ON bi.product_id = p.product_id "
                + "WHERE bi.bucket_id = ?";
        customJdbcTemplate.execute(moveItemsSql, orderId, bucketId);

        String clearBucketSql =
                "DELETE FROM bucket_items WHERE bucket_id = ?";
        customJdbcTemplate.execute(clearBucketSql, bucketId);
    }

    /**
     * Retrieves the formatted purchase history for a specific user.
     *
     * @param userId the ID of the user
     * @return a list of formatted strings representing the user's order history
     */
    public List<String> getOrderHistory(final long userId) {
        String sql = "SELECT o.order_id, o.purchase_date, p.name, "
                + "oi.price_at_purchase, oi.quantity "
                + "FROM orders o "
                + "JOIN order_items oi ON o.order_id = oi.order_id "
                + "JOIN products p ON oi.product_id = p.product_id "
                + "WHERE o.user_id = ? "
                + "ORDER BY o.purchase_date DESC";

        return customJdbcTemplate.findMany(sql, rs -> {
            try {
                long orderId = rs.getLong("order_id");
                java.sql.Timestamp purchaseDate =
                        rs.getTimestamp("purchase_date");
                String productName = rs.getString("name");
                BigDecimal price = rs.getBigDecimal("price_at_purchase");
                int quantity = rs.getInt("quantity");

                return String.format(
                        "Order ID: %d | Date: %s | Product: %s | "
                                + "Price: $%s | Quantity: %d",
                        orderId,
                        purchaseDate.toString(),
                        productName,
                        price.toString(),
                        quantity
                );
            } catch (SQLException e) {
                throw new RuntimeException(
                        "Error mapping Order History from ResultSet", e
                );
            }
        }, userId);
    }
}
