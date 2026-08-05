package com.bobocode.demo.dataBase;

import com.bobocode.utility.CustomJdbcTemplate;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

class IsolationDemoTest {

    private static HikariDataSource dataSource;
    private static CustomJdbcTemplate customJdbcTemplate;
    private static IsolationDemo isolationDemo;

    @BeforeAll
    static void setUp() throws Exception {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        dataSource = new HikariDataSource(config);
        customJdbcTemplate = new CustomJdbcTemplate(dataSource);
        isolationDemo = new IsolationDemo(customJdbcTemplate);

        try (Connection conn = dataSource.getConnection();
             Statement st = conn.createStatement()) {

            st.execute("""
                    CREATE TABLE products(
                        product_id BIGINT PRIMARY KEY,
                        name VARCHAR(255),
                        price NUMERIC(19,2),
                        category_id BIGINT,
                        is_active BOOLEAN
                    )
                    """);

            st.execute("""
                    INSERT INTO products
                    VALUES (1,'Laptop',100,1,true)
                    """);
        }
    }

    @AfterAll
    static void tearDown() {
        dataSource.close();
    }

    @Test
    void shouldDemonstrateReadCommitted() throws Exception {

        CountDownLatch firstRead = new CountDownLatch(1);
        CountDownLatch updateFinished = new CountDownLatch(1);

        Thread reader = new Thread(() ->
                isolationDemo.demonstrateReadCommitted(
                        1L,
                        firstRead,
                        updateFinished));

        Thread updater = new Thread(() -> {
            try {
                firstRead.await();

                isolationDemo.updatePrice(
                        1L,
                        new BigDecimal("200")
                );

                updateFinished.countDown();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        reader.start();
        updater.start();

        reader.join();
        updater.join();
    }

    @Test
    void shouldDemonstrateRepeatableRead() throws Exception {

        customJdbcTemplate.execute(
                "UPDATE products SET price = ? WHERE product_id = ?",
                new BigDecimal("100"),
                1L
        );

        CountDownLatch firstRead = new CountDownLatch(1);
        CountDownLatch updateFinished = new CountDownLatch(1);

        Thread reader = new Thread(() ->
                isolationDemo.demonstrateRepeatableRead(
                        1L,
                        firstRead,
                        updateFinished));

        Thread updater = new Thread(() -> {
            try {
                firstRead.await();

                isolationDemo.updatePrice(
                        1L,
                        new BigDecimal("300")
                );

                updateFinished.countDown();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        reader.start();
        updater.start();

        reader.join();
        updater.join();
    }
}