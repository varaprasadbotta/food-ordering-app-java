package com.prasad.fooddelivery.common.db;

import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class DatabaseConfig {

    private static final String PROPERTIES_FILE = "application.properties";

    private DatabaseConfig() {
    }

    // Builds a DataSource from application.properties, with environment variables taking priority.
    // PGSimpleDataSource opens a new connection each time; it will be swapped for a pooled
    // DataSource (HikariCP) later without any repository code changing.
    public static DataSource createDataSource() {
        Properties properties = loadProperties();

        return createDataSource(
                valueOf("DB_URL", "db.url", properties),
                valueOf("DB_USERNAME", "db.username", properties),
                valueOf("DB_PASSWORD", "db.password", properties)
        );
    }

    public static DataSource createDataSource(String url, String username, String password) {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    // Applies any pending scripts from src/main/resources/db/migration
    public static void migrate(DataSource dataSource) {
        Flyway.configure()
                .dataSource(dataSource)
                .load()
                .migrate();
    }

    private static String valueOf(String envName, String propertyName, Properties properties) {
        String fromEnv = System.getenv(envName);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        String fromFile = properties.getProperty(propertyName);
        if (fromFile == null || fromFile.isBlank()) {
            throw new IllegalStateException("Missing database setting: " + propertyName);
        }
        return fromFile;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + PROPERTIES_FILE, e);
        }

        return properties;
    }
}
