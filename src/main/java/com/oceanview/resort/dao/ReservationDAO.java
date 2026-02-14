package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.ReservationRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class ReservationDAO implements ReservationRepository {
    private final DataSource dataSource;

    public ReservationDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public ReservationDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Reservation create(Reservation reservation) {
        String sql = "INSERT INTO reservations (reservation_no, guest_id, room_id, check_in, check_out, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reservation.getReservationNo());
            stmt.setLong(2, reservation.getGuest().getId());
            stmt.setLong(3, reservation.getRoom().getId());
            stmt.setDate(4, java.sql.Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(5, java.sql.Date.valueOf(reservation.getCheckOutDate()));
            stmt.setString(6, reservation.getStatus().name());
            stmt.setLong(7, reservation.getCreatedBy().getId());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    reservation.setId(keys.getLong(1));
                }
            }
            return reservation;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create reservation", ex);
        }
    }

    @Override
    public Reservation update(Reservation reservation) {
        String sql = "UPDATE reservations SET guest_id = ?, room_id = ?, check_in = ?, check_out = ?, status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reservation.getGuest().getId());
            stmt.setLong(2, reservation.getRoom().getId());
            stmt.setDate(3, java.sql.Date.valueOf(reservation.getCheckInDate()));
            stmt.setDate(4, java.sql.Date.valueOf(reservation.getCheckOutDate()));
            stmt.setString(5, reservation.getStatus().name());
            stmt.setLong(6, reservation.getId());
            stmt.executeUpdate();
            return reservation;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to update reservation", ex);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to delete reservation", ex);
        }
    }

    @Override
    public Reservation findById(long id) {
        String sql = "SELECT r.*, g.first_name, g.last_name, g.email, g.phone, g.address, g.guest_type, rm.room_number, rt.type_name FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.id " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "JOIN room_types rt ON rm.room_type_id = rt.id " +
                "WHERE r.id = ?";
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
            throw new DatabaseException("Failed to find reservation", ex);
        }
    }

    @Override
    public Reservation findByReservationNo(String reservationNo) {
        String sql = "SELECT r.*, g.first_name, g.last_name, g.email, g.phone, g.address, g.guest_type, rm.room_number, rt.type_name FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.id " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "JOIN room_types rt ON rm.room_type_id = rt.id " +
                "WHERE r.reservation_no = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationNo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find reservation by number", ex);
        }
    }

    @Override
    public List<Reservation> findAll() {
        String sql = "SELECT r.*, g.first_name, g.last_name, g.email, g.phone, g.address, g.guest_type, rm.room_number, rt.type_name FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.id " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "JOIN room_types rt ON rm.room_type_id = rt.id";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(mapRow(rs));
            }
            return reservations;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list reservations", ex);
        }
    }

    @Override
    public List<Reservation> search(String keyword) {
        String sql = "SELECT r.*, g.first_name, g.last_name, g.email, g.phone, g.address, g.guest_type, rm.room_number, rt.type_name FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.id " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "JOIN room_types rt ON rm.room_type_id = rt.id " +
                "WHERE r.reservation_no LIKE ? OR CONCAT(g.first_name, ' ', g.last_name) LIKE ? OR rm.room_number LIKE ? OR r.guest_id = ? OR r.id = ? OR g.phone LIKE ? OR g.email LIKE ?";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String like = "%" + (keyword == null ? "" : keyword.trim()) + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);
            stmt.setString(3, like);
            Long numeric = null;
            try {
                numeric = keyword == null ? null : Long.parseLong(keyword.trim());
            } catch (NumberFormatException ignored) {
            }
            long numericValue = numeric == null ? -1 : numeric;
            stmt.setLong(4, numericValue);
            stmt.setLong(5, numericValue);
            stmt.setString(6, like);
            stmt.setString(7, like);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
            return reservations;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to search reservations", ex);
        }
    }

    @Override
    public List<Reservation> findWithFilters(String keyword, LocalDate fromDate, LocalDate toDate, String status) {
        StringBuilder sql = new StringBuilder("SELECT r.*, g.first_name, g.last_name, g.email, g.phone, g.address, g.guest_type, rm.room_number, rt.type_name FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.id " +
                "JOIN rooms rm ON r.room_id = rm.id " +
                "JOIN room_types rt ON rm.room_type_id = rt.id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            String like = "%" + keyword.trim() + "%";
            sql.append(" AND (r.reservation_no LIKE ? OR CONCAT(g.first_name, ' ', g.last_name) LIKE ? OR rm.room_number LIKE ? OR g.phone LIKE ? OR g.email LIKE ?");
            Long numeric = null;
            try {
                numeric = Long.parseLong(keyword.trim());
            } catch (NumberFormatException ignored) {
            }
            if (numeric != null) {
                sql.append(" OR r.guest_id = ? OR r.id = ?)");
                params.add(like);
                params.add(like);
                params.add(like);
                params.add(like);
                params.add(like);
                params.add(numeric);
                params.add(numeric);
            } else {
                sql.append(")");
                params.add(like);
                params.add(like);
                params.add(like);
                params.add(like);
                params.add(like);
            }
        }
        if (fromDate != null) {
            sql.append(" AND r.check_in >= ?");
            params.add(java.sql.Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" AND r.check_out <= ?");
            params.add(java.sql.Date.valueOf(toDate));
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND r.status = ?");
            params.add(status.trim());
        }
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) {
                    stmt.setString(i + 1, (String) p);
                } else if (p instanceof Long) {
                    stmt.setLong(i + 1, (Long) p);
                } else if (p instanceof java.sql.Date) {
                    stmt.setDate(i + 1, (java.sql.Date) p);
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapRow(rs));
                }
            }
            return reservations;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find reservations with filters", ex);
        }
    }

    @Override
    public boolean isRoomAvailable(long roomId, LocalDate checkIn, LocalDate checkOut) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE room_id = ? AND status <> 'CANCELLED' AND check_in < ? AND check_out > ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, roomId);
            stmt.setDate(2, java.sql.Date.valueOf(checkOut));
            stmt.setDate(3, java.sql.Date.valueOf(checkIn));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
            return true;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to check room availability", ex);
        }
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        reservation.setReservationNo(rs.getString("reservation_no"));

        Guest guest = new Guest();
        guest.setId(rs.getLong("guest_id"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setLastName(rs.getString("last_name"));
        guest.setEmail(rs.getString("email"));
        guest.setPhone(rs.getString("phone"));
        guest.setAddress(rs.getString("address"));
        String guestType = rs.getString("guest_type");
        if (guestType != null) {
            guest.setGuestType(com.oceanview.resort.model.enums.GuestType.valueOf(guestType));
        }
        reservation.setGuest(guest);

        Room room = new Room();
        room.setId(rs.getLong("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        if (rs.getString("type_name") != null) {
            com.oceanview.resort.model.RoomType roomType = new com.oceanview.resort.model.RoomType();
            roomType.setTypeName(rs.getString("type_name"));
            room.setRoomType(roomType);
        }
        reservation.setRoom(room);

        reservation.setCheckInDate(rs.getDate("check_in").toLocalDate());
        reservation.setCheckOutDate(rs.getDate("check_out").toLocalDate());
        reservation.setStatus(ReservationStatus.valueOf(rs.getString("status")));

        User user = new User();
        user.setId(rs.getLong("created_by"));
        reservation.setCreatedBy(user);

        if (rs.getTimestamp("created_at") != null) {
            reservation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        return reservation;
    }
}
