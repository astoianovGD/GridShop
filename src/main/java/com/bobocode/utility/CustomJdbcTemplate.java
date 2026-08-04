package com.bobocode.utility;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for handling JDBC operations.
 */
@RequiredArgsConstructor
@Component
public final class CustomJdbcTemplate {

    /**
     * Data source.
     */
    private final DataSource dataSource;


    /**
     * Establishes a connection to the database.
     *
     * @return a database Connection
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Executes an update query with optional arguments.
     *
     * @param query the SQL query to execute
     * @param args  the query parameters
     */
    @SuppressFBWarnings("SQL_INJECTION_JDBC")
    public void execute(final String query, final Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
    }

    /**
     * Executes an update query using a statement customizer consumer.
     *
     * @param query               the SQL query to execute
     * @param statementCustomizer the consumer to customize the statement
     */
    @SuppressFBWarnings("SQL_INJECTION_JDBC")
    public void execute(
            final String query,
            final Consumer<PreparedStatement> statementCustomizer
    ) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            statementCustomizer.accept(stmt);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error executing query with consumer", e
            );
        }
    }

    /**
     * Finds and returns a single record mapping it using the provided function.
     *
     * @param query  the SQL query to execute
     * @param mapper the function to map ResultSet to an object
     * @param args   the query parameters
     * @param <T>    the type of the result object
     * @return the mapped object, or null if not found
     */
    public <T> T findOne(
            final String query,
            final Function<ResultSet, T> mapper,
            final Object... args
    ) {
        List<T> results = findMany(query, mapper, args);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            throw new IllegalStateException(
                    "Expected 1 result, but got " + results.size()
            );
        }
        return results.get(0);
    }

    /**
     * Finds and returns a list of records mapping them using the function.
     *
     * @param query  the SQL query to execute
     * @param mapper the function to map ResultSet rows to objects
     * @param args   the query parameters
     * @param <T>    the type of the objects in the list
     * @return a list of mapped objects
     */
    @SuppressFBWarnings("SQL_INJECTION_JDBC")
    public <T> List<T> findMany(
            final String query,
            final Function<ResultSet, T> mapper,
            final Object... args
    ) {
        List<T> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.apply(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error executing findMany query", e
            );
        }
        return list;
    }

    /**
     * Executes a set of operations within a single database transaction.
     *
     * @param action the consumer
     *               that performs database operations using a Connection
     */
    public void doInTransaction(final Consumer<Connection> action) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                action.accept(conn);
                conn.commit();
            } catch (Exception e) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
                throw new RuntimeException(
                        "Transaction failed and was rolled back", e
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error managing transaction connection", e
            );
        }
    }
}
