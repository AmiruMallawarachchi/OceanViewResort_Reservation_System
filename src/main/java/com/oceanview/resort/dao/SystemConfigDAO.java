package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.config.DatabaseConnection;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemConfigDAO {
    private static final String GET_SQL = "SELECT config_value FROM system_config WHERE config_key = ?";
    private static final String SET_SQL = "INSERT INTO system_config (config_key, config_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE config_value = ?";

    private final DataSource dataSource;

    public SystemConfigDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public SystemConfigDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String get(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_SQL)) {
            stmt.setString(1, key.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("config_value") : null;
            }
        } catch (SQLException ex) {
            // If system_config table does not exist yet, fall back to null so callers can use defaults
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("system_config")) {
                return null;
            }
            throw new DatabaseException("Failed to get config: " + key, ex);
        }
    }

    public void set(String key, String value) {
        if (key == null || key.isBlank()) {
            return;
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SET_SQL)) {
            stmt.setString(1, key.trim());
            stmt.setString(2, value == null ? "" : value);
            stmt.setString(3, value == null ? "" : value);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            // If system_config table is missing, ignore writes instead of breaking the app
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("system_config")) {
                return;
            }
            throw new DatabaseException("Failed to set config: " + key, ex);
        }
    }
}
