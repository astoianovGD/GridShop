package com.bobocode.demo.dataBase;

import com.bobocode.utility.CustomJdbcTemplate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Test class for verifying database consistency and transaction behavior (Lab Task Part 1).
 */
public final class ConsistencyDemoTest {

    private ConsistencyDemoTest() {
    }

    /**
     * Main method to execute the consistency and transaction rollback tests.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        // Set default timezone to avoid compatibility issues
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Kyiv"));

        // Load database connection parameters from environment variables
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("POSTGRES_USER");
        String dbPassword = System.getenv("POSTGRES_PASSWORD");

        // Configure HikariCP data source
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);

        DataSource dataSource = new HikariDataSource(config);
        CustomJdbcTemplate customJdbcTemplate = new CustomJdbcTemplate(dataSource);

        // Setup test data to ensure the user, bucket, and products exist
        long testUserId = setupTestData(customJdbcTemplate);

        // Initialize the demonstration service
        ConsistencyDemo consistencyDemo = new ConsistencyDemo(customJdbcTemplate);

        // Test 1: Run without transaction to demonstrate inconsistent state
        try {
            System.out.println("--- Running Test 1: WITHOUT transaction ---");
            consistencyDemo.demonstrateInconsistency(testUserId);
        } catch (Exception e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

        // Test 2: Run with transaction rollback to demonstrate safe behavior
        try {
            System.out.println("\n--- Running Test 2: WITH transaction rollback ---");
            consistencyDemo.demonstrateTransactionRollback(testUserId);
        } catch (Exception e) {
            System.out.println("Caught expected transaction exception: " + e.getMessage());
        }
    }

    /**
     * Helper method to prepare test data in the database before running tests.
     *
     * @param customJdbcTemplate the JDBC template
     * @return the test user ID
     */
    private static long setupTestData(CustomJdbcTemplate customJdbcTemplate) {
        // Step 1: Ensure a test user exists
        String insertUserSql = "INSERT INTO users (email, password, lastname, firstname, role_id) "
                + "VALUES ('test_consistency@gmail.com', 'password123', 'Doe', 'John', 3) "
                + "ON CONFLICT (email) DO NOTHING";
        customJdbcTemplate.execute(insertUserSql);

        // Retrieve the test user ID
        Long userId = customJdbcTemplate.findOne("SELECT user_id FROM users WHERE email = 'test_consistency@gmail.com'", rs -> {
            try { return rs.getLong("user_id"); } catch (Exception e) { throw new RuntimeException(e); }
        });

        // Step 2: Ensure a bucket exists for this user
        String insertBucketSql = "INSERT INTO bucket (user_id) VALUES (?) ON CONFLICT (user_id) DO NOTHING";
        customJdbcTemplate.execute(insertBucketSql, userId);

        Long bucketId = customJdbcTemplate.findOne("SELECT bucket_id FROM bucket WHERE user_id = ?", rs -> {
            try { return rs.getLong("bucket_id"); } catch (Exception e) { throw new RuntimeException(e); }
        }, userId);

        // Step 3: Ensure a category and product exist, then add the product to the bucket
        Long productId = customJdbcTemplate.findOne("SELECT product_id FROM products LIMIT 1", rs -> {
            try { return rs.getLong("product_id"); } catch (Exception e) { throw new RuntimeException(e); }
        });

        if (productId == null) {
            customJdbcTemplate.execute("INSERT INTO categories (name) VALUES ('Test Category') ON CONFLICT (name) DO NOTHING");
            Long catId = customJdbcTemplate.findOne("SELECT category_id FROM categories WHERE name = 'Test Category'", rs -> {
                try { return rs.getLong("category_id"); } catch (Exception e) { throw new RuntimeException(e); }
            });
            customJdbcTemplate.execute("INSERT INTO products (name, price, category_id) VALUES ('Test Product', 99.99, ?)", catId);
            productId = customJdbcTemplate.findOne("SELECT product_id FROM products WHERE name = 'Test Product'", rs -> {
                try { return rs.getLong("product_id"); } catch (Exception e) { throw new RuntimeException(e); }
            });
        }

        // Add product to bucket items
        String insertBucketItemSql = "INSERT INTO bucket_items (bucket_id, product_id, quantity) VALUES (?, ?, 2) "
                + "ON CONFLICT (bucket_id, product_id) DO UPDATE SET quantity = 2";
        customJdbcTemplate.execute(insertBucketItemSql, bucketId, productId);

        System.out.println("Test data successfully prepared for user ID: " + userId);
        return userId;
    }
}
