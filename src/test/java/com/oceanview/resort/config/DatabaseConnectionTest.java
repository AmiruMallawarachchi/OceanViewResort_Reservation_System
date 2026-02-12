package com.oceanview.resort.config;

import org.junit.Assert;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionTest {

    @Test
    public void testGetDataSourceNotNull() {
        DataSource dataSource = DatabaseConnection.getDataSource();
        Assert.assertNotNull("DataSource should not be null", dataSource);
    }

    @Test
    public void testGetConnectionReturnsValidConnection() throws SQLException {
        DataSource dataSource = DatabaseConnection.getDataSource();

        try (Connection connection = dataSource.getConnection()) {
            Assert.assertNotNull("Connection should not be null", connection);
            Assert.assertFalse("Connection should not be closed", connection.isClosed());
        }
    }

    @Test
    public void testCloseQuietlyDoesNotThrowOnNull() {
        // Should not throw any exception
        DatabaseConnection.closeQuietly(null);
    }
}

