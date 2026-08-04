package com.bobocode.demo;

import com.bobocode.utility.CustomJdbcTemplate;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

/**
 * Demonstrates transaction isolation levels.
 */
@RequiredArgsConstructor
public final class IsolationDemo {

    /**
     * JDBC template for database operations.
     */
    private final CustomJdbcTemplate customJdbcTemplate;

    /**
     * Demonstrates the READ_COMMITTED isolation level.
     *
     * @param productId the product identifier
     * @param firstReadLatch latch released after the first read
     * @param updateFinishedLatch latch released after update completion
     */
    public void demonstrateReadCommitted(
            final long productId,
            final CountDownLatch firstReadLatch,
            final CountDownLatch updateFinishedLatch
    ) {

        customJdbcTemplate.doInTransaction(conn -> {
            try {
                conn.setTransactionIsolation(
                        Connection.TRANSACTION_READ_COMMITTED
                );

                BigDecimal firstPrice = getProductPrice(conn, productId);
                System.out.println("READ_COMMITTED - first read: "
                        + firstPrice
                );

                firstReadLatch.countDown();

                updateFinishedLatch.await();

                BigDecimal secondPrice = getProductPrice(conn, productId);
                System.out.println("READ_COMMITTED - second read: "
                        + secondPrice
                );

                if (!firstPrice.equals(secondPrice)) {
                    System.out.println(
                            "Non-repeatable read detected under READ_COMMITTED."
                    );
                } else {
                    System.out.println(
                            "Prices are equal under READ_COMMITTED."
                    );
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Demonstrates the REPEATABLE_READ isolation level.
     *
     * @param productId the product identifier
     * @param firstReadLatch latch released after the first read
     * @param updateFinishedLatch latch released after update completion
     */
    public void demonstrateRepeatableRead(
            final long productId,
            final CountDownLatch firstReadLatch,
            final CountDownLatch updateFinishedLatch
    ) {

        customJdbcTemplate.doInTransaction(conn -> {
            try {
                conn.setTransactionIsolation(
                        Connection.TRANSACTION_REPEATABLE_READ
                );

                BigDecimal firstPrice = getProductPrice(conn, productId);
                System.out.println("REPEATABLE_READ - first read: "
                        + firstPrice
                );

                firstReadLatch.countDown();

                updateFinishedLatch.await();

                BigDecimal secondPrice = getProductPrice(conn, productId);
                System.out.println("REPEATABLE_READ - second read: "
                        + secondPrice
                );

                if (!firstPrice.equals(secondPrice)) {
                    System.out.println(
                            "Unexpected! Price changed under REPEATABLE_READ."
                    );
                } else {
                    System.out.println(
                            "Price remained unchanged under REPEATABLE_READ."
                    );
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Updates the product price.
     *
     * @param productId the product identifier
     * @param newPrice the new product price
     */
    public void updatePrice(
            final long productId,
            final BigDecimal newPrice
    ) {

        customJdbcTemplate.execute(
                "UPDATE products SET price = ? WHERE product_id = ?",
                newPrice,
                productId
        );
    }

    /**
     * Returns the current product price.
     *
     * @param conn active database connection
     * @param productId the product identifier
     * @return current product price
     * @throws SQLException if the product cannot be read
     */
    private BigDecimal getProductPrice(
            final Connection conn,
            final long productId
    ) throws SQLException {

        String sql =
                "SELECT price FROM products WHERE product_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getBigDecimal("price");
                }
            }
        }

        throw new SQLException("Product not found.");
    }
}
