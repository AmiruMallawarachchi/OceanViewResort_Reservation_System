package com.oceanview.resort.dao;

import com.oceanview.resort.exception.DatabaseException;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import com.oceanview.resort.model.User;
import com.oceanview.resort.repository.ReportRepository;
import com.oceanview.resort.config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class ReportDAO implements ReportRepository {
    private final DataSource dataSource;

    public ReportDAO() {
        this(DatabaseConnection.getDataSource());
    }

    public ReportDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Report create(Report report) {
        String sql = "INSERT INTO reports (report_type, format, generated_by, parameters, content) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, report.getReportType() == null ? null : report.getReportType().name());
            stmt.setString(2, report.getFormat() == null ? null : report.getFormat().name());
            stmt.setLong(3, report.getGeneratedBy().getId());
            stmt.setString(4, report.getParameters());
            stmt.setBytes(5, report.getContent());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getLong(1));
                }
            }
            return report;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to create report", ex);
        }
    }

    @Override
    public Report findById(long id) {
        String sql = "SELECT * FROM reports WHERE id = ?";
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
            throw new DatabaseException("Failed to find report", ex);
        }
    }

    @Override
    public List<Report> findAll() {
        String sql = "SELECT * FROM reports";
        List<Report> reports = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reports.add(mapRow(rs));
            }
            return reports;
        } catch (SQLException ex) {
            throw new DatabaseException("Failed to list reports", ex);
        }
    }

    private Report mapRow(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getLong("id"));
        String type = rs.getString("report_type");
        if (type != null) {
            report.setReportType(ReportType.valueOf(type));
        }
        String format = rs.getString("format");
        if (format != null) {
            report.setFormat(ReportFormat.valueOf(format));
        }
        User user = new User();
        user.setId(rs.getLong("generated_by"));
        report.setGeneratedBy(user);
        if (rs.getTimestamp("generated_at") != null) {
            report.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        }
        report.setParameters(rs.getString("parameters"));
        report.setContent(rs.getBytes("content"));
        return report;
    }
}
