package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomTypeRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class RoomTypeDAO implements RoomTypeRepository {
    private final DataSource dataSource;

    public RoomTypeDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public RoomTypeDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public RoomType create(RoomType roomType) {
        String sql = "INSERT INTO room_types (type_name, description, rate_per_night, max_occupancy, amenities, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, roomType.getTypeName());
            stmt.setString(2, roomType.getDescription());
            stmt.setBigDecimal(3, roomType.getRatePerNight());
            stmt.setInt(4, roomType.getMaxOccupancy());
            stmt.setString(5, roomType.getAmenities());
            stmt.setBoolean(6, roomType.isActive());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    roomType.setId(keys.getLong(1));
                }
            }
            return roomType;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create room type", ex);
        }
    }

    @Override
    public RoomType update(RoomType roomType) {
        String sql = "UPDATE room_types SET type_name = ?, description = ?, rate_per_night = ?, max_occupancy = ?, amenities = ?, is_active = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomType.getTypeName());
            stmt.setString(2, roomType.getDescription());
            stmt.setBigDecimal(3, roomType.getRatePerNight());
            stmt.setInt(4, roomType.getMaxOccupancy());
            stmt.setString(5, roomType.getAmenities());
            stmt.setBoolean(6, roomType.isActive());
            stmt.setLong(7, roomType.getId());
            stmt.executeUpdate();
            return roomType;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to update room type", ex);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM room_types WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to delete room type", ex);
        }
    }

    @Override
    public RoomType findById(long id) {
        String sql = "SELECT * FROM room_types WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find room type", ex);
        }
    }

    @Override
    public List<RoomType> findAll() {
        String sql = "SELECT * FROM room_types";
        List<RoomType> types = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                types.add(mapRow(rs));
            }
            return types;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list room types", ex);
        }
    }

    private RoomType mapRow(ResultSet rs) throws SQLException {
        RoomType roomType = new RoomType();
        roomType.setId(rs.getLong("id"));
        roomType.setTypeName(rs.getString("type_name"));
        roomType.setDescription(rs.getString("description"));
        roomType.setRatePerNight(rs.getBigDecimal("rate_per_night"));
        roomType.setMaxOccupancy(rs.getInt("max_occupancy"));
        roomType.setAmenities(rs.getString("amenities"));
        roomType.setActive(rs.getBoolean("is_active"));
        return roomType;
    }
}
