package com.bobocode;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolPerformanceTest {

    @Test
    void comparePerformanceWithHikari() throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test_pool;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(10);

        DataSource dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE products (
                        product_id BIGINT PRIMARY KEY,
                        name VARCHAR(255),
                        price DECIMAL(19,2)
                    )
                    """);

            statement.execute("""
                    INSERT INTO products
                    VALUES (1,'Laptop',100.00)
                    """);
        }

        int numberOfThreads = 10;
        List<Thread> threads = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {

            Thread thread = new Thread(() -> {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt =
                             conn.prepareStatement(
                                     "SELECT * FROM products WHERE product_id = ?")) {

                    stmt.setLong(1, 1L);
                    stmt.executeQuery();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();

        System.out.println(
                "Execution time with HikariCP (" +
                        numberOfThreads +
                        " threads): " +
                        (endTime - startTime) +
                        " ms"
        );

        ((HikariDataSource) dataSource).close();
    }
}