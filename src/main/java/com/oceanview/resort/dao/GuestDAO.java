package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.repository.GuestRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class GuestDAO implements GuestRepository {
    private final DataSource dataSource;

    public GuestDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public GuestDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Guest create(Guest guest) {
        String sql = "INSERT INTO guests (first_name, last_name, email, phone, address, id_type, id_number, nationality, guest_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, guest.getFirstName());
            stmt.setString(2, guest.getLastName());
            stmt.setString(3, guest.getEmail());
            stmt.setString(4, guest.getPhone());
            stmt.setString(5, guest.getAddress());
            stmt.setString(6, guest.getIdType());
            stmt.setString(7, guest.getIdNumber());
            stmt.setString(8, guest.getNationality());
            stmt.setString(9, guest.getGuestType() == null ? "REGULAR" : guest.getGuestType().name());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    guest.setId(keys.getLong(1));
                }
            }
            return guest;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create guest", ex);
        }
    }

    @Override
    public Guest update(Guest guest) {
        String sql = "UPDATE guests SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, id_type = ?, id_number = ?, nationality = ?, guest_type = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guest.getFirstName());
            stmt.setString(2, guest.getLastName());
            stmt.setString(3, guest.getEmail());
            stmt.setString(4, guest.getPhone());
            stmt.setString(5, guest.getAddress());
            stmt.setString(6, guest.getIdType());
            stmt.setString(7, guest.getIdNumber());
            stmt.setString(8, guest.getNationality());
            stmt.setString(9, guest.getGuestType() == null ? "REGULAR" : guest.getGuestType().name());
            stmt.setLong(10, guest.getId());
            stmt.executeUpdate();
            return guest;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to update guest", ex);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to delete guest", ex);
        }
    }

    @Override
    public Guest findById(long id) {
        String sql = "SELECT * FROM guests WHERE id = ?";
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
            throw new DatabaseException("Failed to find guest", ex);
        }
    }

    @Override
    public Guest findByIdNumber(String idNumber) {
        String sql = "SELECT * FROM guests WHERE id_number = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find guest by id number", ex);
        }
    }

    @Override
    public List<Guest> findAll() {
        String sql = "SELECT * FROM guests";
        List<Guest> guests = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                guests.add(mapRow(rs));
            }
            return guests;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list guests", ex);
        }
    }

    private Guest mapRow(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setLastName(rs.getString("last_name"));
        guest.setEmail(rs.getString("email"));
        guest.setPhone(rs.getString("phone"));
        guest.setAddress(rs.getString("address"));
        guest.setIdType(rs.getString("id_type"));
        guest.setIdNumber(rs.getString("id_number"));
        guest.setNationality(rs.getString("nationality"));
        String guestType = rs.getString("guest_type");
        if (guestType != null) {
            guest.setGuestType(com.oceanview.resort.model.enums.GuestType.valueOf(guestType));
        }
        if (rs.getTimestamp("created_at") != null) {
            guest.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return guest;
    }
}
