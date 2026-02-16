package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.enums.RoomStatus;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.repository.RoomRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.CallableStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class RoomDAO implements RoomRepository {
    private final DataSource dataSource;

    public RoomDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public RoomDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Room create(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type_id, floor, status, description, is_full_access) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setLong(2, room.getRoomType().getId());
            stmt.setInt(3, room.getFloor());
            stmt.setString(4, room.getStatus() == null ? null : room.getStatus().name());
            stmt.setString(5, room.getDescription());
            stmt.setBoolean(6, room.isFullAccess());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    room.setId(keys.getLong(1));
                }
            }
            return room;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create room", ex);
        }
    }

    @Override
    public Room update(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type_id = ?, floor = ?, status = ?, description = ?, is_full_access = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setLong(2, room.getRoomType().getId());
            stmt.setInt(3, room.getFloor());
            stmt.setString(4, room.getStatus() == null ? null : room.getStatus().name());
            stmt.setString(5, room.getDescription());
            stmt.setBoolean(6, room.isFullAccess());
            stmt.setLong(7, room.getId());
            stmt.executeUpdate();
            return room;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to update room", ex);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to delete room", ex);
        }
    }

    @Override
    public Room findById(long id) {
        String sql = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.max_occupancy, rt.amenities FROM rooms r " +
                "JOIN room_types rt ON r.room_type_id = rt.id WHERE r.id = ?";
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
            throw new DatabaseException("Failed to find room", ex);
        }
    }

    @Override
    public Room findByRoomNumber(String roomNumber) {
        String sql = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.max_occupancy, rt.amenities FROM rooms r " +
                "JOIN room_types rt ON r.room_type_id = rt.id WHERE r.room_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roomNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find room by number", ex);
        }
    }

    @Override
    public List<Room> findAll() {
        String sql = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.max_occupancy, rt.amenities FROM rooms r " +
                "JOIN room_types rt ON r.room_type_id = rt.id";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
            return rooms;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list rooms", ex);
        }
    }

    @Override
    public List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.max_occupancy, rt.amenities FROM rooms r " +
                "JOIN room_types rt ON r.room_type_id = rt.id " +
                "WHERE r.status = 'AVAILABLE' AND r.id NOT IN (" +
                "SELECT room_id FROM reservations WHERE status <> 'CANCELLED' AND check_in < ? AND check_out > ?" +
                ")";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(checkOut));
            stmt.setDate(2, java.sql.Date.valueOf(checkIn));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
            return rooms;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find available rooms", ex);
        }
    }

    @Override
    public List<Room> findAvailableUsingProcedure(LocalDate checkIn, LocalDate checkOut) {
        List<Long> roomIds = new ArrayList<>();
        String callSql = "{CALL get_available_room_ids(?, ?)}";
        try (Connection conn = dataSource.getConnection();
             CallableStatement call = conn.prepareCall(callSql)) {
            call.setDate(1, java.sql.Date.valueOf(checkIn));
            call.setDate(2, java.sql.Date.valueOf(checkOut));
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    roomIds.add(rs.getLong(1));
                }
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to call stored procedure get_available_room_ids", ex);
        }
        if (roomIds.isEmpty()) {
            return new ArrayList<>();
        }
        StringBuilder sql = new StringBuilder(
                "SELECT r.*, rt.type_name, rt.rate_per_night, rt.max_occupancy, rt.amenities FROM rooms r " +
                        "JOIN room_types rt ON r.room_type_id = rt.id WHERE r.id IN (");
        for (int i = 0; i < roomIds.size(); i++) {
            if (i > 0) {
                sql.append(',');
            }
            sql.append('?');
        }
        sql.append(')');

        List<Room> rooms = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < roomIds.size(); i++) {
                stmt.setLong(i + 1, roomIds.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(mapRow(rs));
                }
            }
            return rooms;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to load rooms from stored procedure result", ex);
        }
    }

    @Override
    public int countByRoomTypeId(long roomTypeId) {
        String sql = "SELECT COUNT(*) FROM rooms WHERE room_type_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, roomTypeId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to count rooms by room type", ex);
        }
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setRoomNumber(rs.getString("room_number"));
        RoomType roomType = new RoomType();
        roomType.setId(rs.getLong("room_type_id"));
        roomType.setTypeName(rs.getString("type_name"));
        roomType.setRatePerNight(rs.getBigDecimal("rate_per_night"));
        roomType.setMaxOccupancy(rs.getInt("max_occupancy"));
        roomType.setAmenities(rs.getString("amenities"));
        room.setRoomType(roomType);
        room.setFloor(rs.getInt("floor"));
        String status = rs.getString("status");
        if (status != null) {
            room.setStatus(RoomStatus.valueOf(status));
        }
        room.setDescription(rs.getString("description"));
        room.setFullAccess(rs.getBoolean("is_full_access"));
        return room;
    }
}
