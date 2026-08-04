package com.bobocode.demo;

import com.bobocode.utility.CustomJdbcTemplate;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Demonstration class for database consistency and transaction behavior
 * (Lab Task Part 1).
 */
@RequiredArgsConstructor
public final class ConsistencyDemo {

    /** The JDBC template for database operations. */
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Demonstrates an inconsistent database state by executing operations
     * WITHOUT a transaction
     * and throwing an exception before cleaning the bucket.
     *
     * @param userId the ID of the user performing the order
     */
    public void demonstrateInconsistency(final long userId) {
        Long bucketId = getBucketId(userId);
        if (bucketId == null) {
            System.out.println("User does not have a bucket for testing!");
            return;
        }

        // Step 1: Create an order in the orders table
        String createOrderSql = "INSERT INTO orders (user_id, purchase_date) "
                + "VALUES (?, CURRENT_TIMESTAMP) RETURNING order_id";
        Long orderId = customJdbcTemplate.findOne(createOrderSql, rs -> {
            try {
                return rs.getLong("order_id");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, userId);

        // Step 2: Move items from bucket to order_items
        String moveItemsSql = "INSERT INTO order_items (order_id, product_id, "
                + "price_at_purchase, quantity) SELECT ?, bi.product_id, "
                + "p.price, bi.quantity FROM bucket_items bi JOIN products p "
                + "ON bi.product_id = p.product_id WHERE bi.bucket_id = ?";
        customJdbcTemplate.execute(moveItemsSql, orderId, bucketId);

        System.out.println("❌ Simulated failure without transaction: "
                + "order created, but bucket is not cleared!");
        throw new RuntimeException("Simulated crash without transaction!");
    }

    /**
     * Demonstrates a safe system behavior WITH a transaction.
     * Rolls back all changes if an error occurs.
     *
     * @param userId the ID of the user performing the order
     */
    public void demonstrateTransactionRollback(final long userId) {
        customJdbcTemplate.doInTransaction(conn -> {
            try {
                Long bucketId = getBucketIdConn(conn, userId);
                if (bucketId == null) {
                    return;
                }

                Long orderId = createOrderConn(conn, userId);
                moveItemsConn(conn, orderId, bucketId);

                System.out.println("⚠️ Simulating error inside transaction... "
                        + "ROLLBACK should be triggered!");
                throw new RuntimeException(
                        "Simulated crash inside transaction!"
                );
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Helper method to retrieve bucket ID.
     *
     * @param userId the ID of the user
     * @return the bucket ID, or null if not found
     */
    private Long getBucketId(final long userId) {
        String sql = "SELECT bucket_id FROM bucket WHERE user_id = ?";
        return customJdbcTemplate.findOne(sql, rs -> {
            try {
                return rs.getLong("bucket_id");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, userId);
    }

    /**
     * Helper method to retrieve bucket ID using a specific SQL connection.
     *
     * @param conn   the active database connection
     * @param userId the ID of the user
     * @return the bucket ID, or null if not found
     * @throws SQLException if a database error occurs
     */
    private Long getBucketIdConn(final Connection conn, final long userId)
            throws SQLException {
        String sql = "SELECT bucket_id FROM bucket WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("bucket_id");
                }
            }
        }
        return null;
    }

    /**
     * Helper method to create an order using a specific SQL connection.
     *
     * @param conn   the active database connection
     * @param userId the ID of the user
     * @return the newly created order ID
     * @throws SQLException if order creation fails
     */
    private Long createOrderConn(final Connection conn, final long userId)
            throws SQLException {
        String sql = "INSERT INTO orders (user_id, purchase_date) "
                + "VALUES (?, CURRENT_TIMESTAMP) RETURNING order_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("order_id");
                }
            }
        }
        throw new SQLException("Failed to create order");
    }

    /**
     * Helper method to move items
     * from bucket to order items using a connection.
     *
     * @param conn     the active database connection
     * @param orderId  the ID of the order
     * @param bucketId the ID of the bucket
     * @throws SQLException if a database error occurs
     */
    private void moveItemsConn(final Connection conn, final long orderId,
                               final long bucketId) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, "
                + "price_at_purchase, quantity) SELECT ?, bi.product_id, "
                + "p.price, bi.quantity FROM bucket_items bi JOIN products p "
                + "ON bi.product_id = p.product_id WHERE bi.bucket_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderId);
            stmt.setLong(2, bucketId);
            stmt.executeUpdate();
        }
    }
}
