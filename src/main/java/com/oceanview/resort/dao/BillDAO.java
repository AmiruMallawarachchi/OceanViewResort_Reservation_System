package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.Bill;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.BillRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class BillDAO implements BillRepository {
    private final DataSource dataSource;

    public BillDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public BillDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Bill create(Bill bill) {
        String sql = "INSERT INTO bills (bill_no, reservation_id, number_of_nights, room_rate, total_amount, discount_amount, tax_amount, net_amount, generated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, bill.getBillNo());
            stmt.setLong(2, bill.getReservation().getId());
            stmt.setInt(3, bill.getNumberOfNights());
            stmt.setBigDecimal(4, bill.getRoomRate());
            stmt.setBigDecimal(5, bill.getTotalAmount());
            stmt.setBigDecimal(6, bill.getDiscountAmount());
            stmt.setBigDecimal(7, bill.getTaxAmount());
            stmt.setBigDecimal(8, bill.getNetAmount());
            stmt.setLong(9, bill.getGeneratedBy().getId());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    bill.setId(keys.getLong(1));
                }
            }
            return bill;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create bill", ex);
        }
    }

    @Override
    public Bill findById(long id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
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
            throw new DatabaseException("Failed to find bill", ex);
        }
    }

    @Override
    public Bill findByReservationId(long reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to find bill by reservation", ex);
        }
    }

    @Override
    public List<Bill> findAll() {
        String sql = "SELECT * FROM bills";
        List<Bill> bills = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bills.add(mapRow(rs));
            }
            return bills;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list bills", ex);
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getLong("id"));
        bill.setBillNo(rs.getString("bill_no"));

        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("reservation_id"));
        bill.setReservation(reservation);

        bill.setNumberOfNights(rs.getInt("number_of_nights"));
        bill.setRoomRate(rs.getBigDecimal("room_rate"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        bill.setTaxAmount(rs.getBigDecimal("tax_amount"));
        bill.setNetAmount(rs.getBigDecimal("net_amount"));

        User user = new User();
        user.setId(rs.getLong("generated_by"));
        bill.setGeneratedBy(user);

        if (rs.getTimestamp("generated_at") != null) {
            bill.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        }
        return bill;
    }
}
