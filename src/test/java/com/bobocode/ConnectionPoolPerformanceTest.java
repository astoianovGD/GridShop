package com.bobocode;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPoolPerformanceTest {

    @Test
    void comparePerformanceWithHikari() throws InterruptedException {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Europe/Kyiv"));

        String url = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : "jdbc:postgresql://localhost:5432/applicationDB";
        String user = System.getenv("POSTGRES_USER") != null ? System.getenv("POSTGRES_USER") : "alex";
        String password = System.getenv("POSTGRES_PASSWORD") != null ? System.getenv("POSTGRES_PASSWORD") : "burmalda";

        int numberOfThreads = 10;
        List<Thread> threads = new ArrayList<>();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);

        DataSource dataSource = new HikariDataSource(config);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numberOfThreads; i++) {
            Thread thread = new Thread(() -> {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT pg_sleep(1)")) {
                    stmt.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time with HikariCP (" + numberOfThreads + " threads): " + (endTime - startTime) + " ms");
    }
}