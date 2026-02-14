package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.repository.DiscountRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class DiscountDAO implements DiscountRepository {
    private final DataSource dataSource;

    public DiscountDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public DiscountDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Discount create(Discount discount) {
        String sql = "INSERT INTO discounts (name, discount_type, guest_type, percent, description, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, discount.getName());
            stmt.setString(2, discount.getDiscountType() == null ? null : discount.getDiscountType().name());
            stmt.setString(3, discount.getGuestType() == null ? null : discount.getGuestType().name());
            stmt.setBigDecimal(4, discount.getPercent());
            stmt.setString(5, discount.getDescription());
            stmt.setBoolean(6, discount.isActive());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    discount.setId(keys.getLong(1));
                }
            }
            return discount;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create discount", ex);
        }
    }

    @Override
    public Discount update(Discount discount) {
        String sql = "UPDATE discounts SET name = ?, discount_type = ?, guest_type = ?, percent = ?, description = ?, is_active = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, discount.getName());
            stmt.setString(2, discount.getDiscountType() == null ? null : discount.getDiscountType().name());
            stmt.setString(3, discount.getGuestType() == null ? null : discount.getGuestType().name());
            stmt.setBigDecimal(4, discount.getPercent());
            stmt.setString(5, discount.getDescription());
            stmt.setBoolean(6, discount.isActive());
            stmt.setLong(7, discount.getId());
            stmt.executeUpdate();
            return discount;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to update discount", ex);
        }
    }

    @Override
    public boolean delete(long id) {
        String sql = "DELETE FROM discounts WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to delete discount", ex);
        }
    }

    @Override
    public Discount findById(long id) {
        String sql = "SELECT * FROM discounts WHERE id = ?";
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
            throw new DatabaseException("Failed to find discount", ex);
        }
    }

    @Override
    public List<Discount> findAll() {
        String sql = "SELECT * FROM discounts ORDER BY id DESC";
        List<Discount> discounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                discounts.add(mapRow(rs));
            }
            return discounts;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list discounts", ex);
        }
    }

    @Override
    public List<Discount> findActive() {
        String sql = "SELECT * FROM discounts WHERE is_active = 1";
        List<Discount> discounts = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                discounts.add(mapRow(rs));
            }
            return discounts;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list active discounts", ex);
        }
    }

    private Discount mapRow(ResultSet rs) throws SQLException {
        Discount discount = new Discount();
        discount.setId(rs.getLong("id"));
        discount.setName(rs.getString("name"));
        String type = rs.getString("discount_type");
        if (type != null) {
            discount.setDiscountType(DiscountType.valueOf(type));
        }
        String guestType = rs.getString("guest_type");
        if (guestType != null) {
            discount.setGuestType(GuestType.valueOf(guestType));
        }
        discount.setPercent(rs.getBigDecimal("percent"));
        discount.setDescription(rs.getString("description"));
        discount.setActive(rs.getBoolean("is_active"));
        if (rs.getTimestamp("created_at") != null) {
            discount.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }
        if (rs.getTimestamp("updated_at") != null) {
            discount.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        }
        return discount;
    }
}
