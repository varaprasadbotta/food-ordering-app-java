package com.prasad.fooddelivery.common.db;

import org.testcontainers.postgresql.PostgreSQLContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

// One PostgreSQL container shared by every repository test in the run ("singleton container").
// It starts on first use, gets the real Flyway migrations, and is removed automatically
// by Testcontainers when the test JVM exits.
public final class PostgresTestDatabase {

    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");
    private static final DataSource DATA_SOURCE;

    static {
        POSTGRES.start();

        DATA_SOURCE = DatabaseConfig.createDataSource(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword()
        );
        DatabaseConfig.migrate(DATA_SOURCE);
    }

    private PostgresTestDatabase() {
    }

    public static DataSource dataSource() {
        return DATA_SOURCE;
    }

    // Call in @BeforeEach so every test starts from empty tables
    public static void clearAllTables() {
        try (Connection connection = DATA_SOURCE.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    TRUNCATE TABLE customer_addresses, menu_items, restaurant_cuisines,
                                   restaurants, customers
                    """);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not clear test tables", e);
        }
    }
}
