package com.oceanview.resort.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provides database connections via a HikariCP connection pool. Uses DataSource
 * internally for production-ready connection management. Configuration is loaded
 * from db.properties.
 */
public final class DatabaseConnection {
    private static final String PROPERTIES_FILE = "db.properties";
    private static final HikariDataSource DATA_SOURCE;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Missing " + PROPERTIES_FILE + " in classpath.");
            }
            Properties props = new Properties();
            props.load(input);

            String driver = props.getProperty("db.driver");
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            if (driver != null && !driver.isBlank()) {
                config.setDriverClassName(driver);
            }

            // Optional pool tuning from properties (with sensible defaults)
            config.setMaximumPoolSize(getInt(props, "db.pool.maxSize", 10));
            config.setMinimumIdle(getInt(props, "db.pool.minIdle", 2));
            config.setConnectionTimeout(getLong(props, "db.pool.connectionTimeout", 30000));
            config.setIdleTimeout(getLong(props, "db.pool.idleTimeout", 600000));
            config.setMaxLifetime(getLong(props, "db.pool.maxLifetime", 1800000));


            DATA_SOURCE = new HikariDataSource(config);
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError("Failed to initialize database connection: " + ex.getMessage());
        }
    }

    private static int getInt(Properties props, String key, int defaultValue) {
        String val = props.getProperty(key);
        if (val == null || val.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;

        }
    }

    private static long getLong(Properties props, String key, long defaultValue) {
        String val = props.getProperty(key);
        if (val == null || val.isBlank()) {
            return defaultValue;
        }
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private DatabaseConnection() {
    }

    /**
     * Obtains a connection from the HikariCP pool.
     */
    public static Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }

    /**
     * Returns the underlying DataSource for advanced use cases (e.g. injection into DAOs).
     */
    public static javax.sql.DataSource getDataSource() {
        return DATA_SOURCE;
    }

    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Shuts down the connection pool. Call during application shutdown.
     */
    public static void shutdown() {
        if (DATA_SOURCE != null && !DATA_SOURCE.isClosed()) {
            DATA_SOURCE.close();
        }
    }
}
