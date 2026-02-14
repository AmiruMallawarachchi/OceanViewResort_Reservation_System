package com.oceanview.resort.daoTest;

import com.oceanview.resort.config.DatabaseConnection;
import com.oceanview.resort.dao.ReportDAO;
import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import com.oceanview.resort.security.PasswordUtil;
import com.oceanview.resort.model.enums.UserRole;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Minimal integration tests for ReportDAO (create, findById, findAll).
 * Creates a User for generated_by and cleans up report and user after.
 */
public class ReportDaoTest {

    private ReportDAO reportDAO;
    private UserDAO userDAO;
    private long createdReportId;
    private long createdUserId;
    private String testUsername;

    @Before
    public void setup() {
        reportDAO = new ReportDAO();
        userDAO = new UserDAO();
        createdReportId = 0;
        createdUserId = 0;
        testUsername = "reportdao_" + System.currentTimeMillis();

        User user = new User();
        user.setUsername(testUsername);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Report DAO Test User");
        user.setEmail(testUsername + "@test.com");
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        createdUserId = userDAO.create(user).getId();
    }

    @After
    public void tearDown() {
        if (createdReportId > 0) {
            deleteReportById(createdReportId);
        }
        if (createdUserId > 0) {
            try {
                userDAO.delete(createdUserId);
            } catch (Exception ignored) {
            }
        } else if (testUsername != null) {
            deleteUserByUsername(testUsername);
        }
    }

    @Test
    public void testCreateAndFindById() {
        Report report = buildReport();
        Report created = reportDAO.create(report);

        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdReportId = created.getId();

        Report found = reportDAO.findById(createdReportId);
        Assert.assertNotNull(found);
        Assert.assertEquals(createdReportId, found.getId());
        Assert.assertEquals(ReportType.OCCUPANCY, found.getReportType());
        Assert.assertEquals(ReportFormat.PDF, found.getFormat());
        Assert.assertEquals("TDD params", found.getParameters());
        Assert.assertNotNull(found.getContent());
        Assert.assertEquals(3, found.getContent().length);
    }

    @Test
    public void testFindAllContainsCreatedReport() {
        Report created = reportDAO.create(buildReport());
        createdReportId = created.getId();

        List<Report> all = reportDAO.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(r -> createdReportId == r.getId()));
    }

    private Report buildReport() {
        Report r = new Report();
        r.setReportType(ReportType.OCCUPANCY);
        r.setFormat(ReportFormat.PDF);
        User generatedBy = new User();
        generatedBy.setId(createdUserId);
        r.setGeneratedBy(generatedBy);
        r.setParameters("TDD params");
        r.setContent(new byte[]{1, 2, 3});
        return r;
    }

    private void deleteReportById(long id) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM reports WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    private void deleteUserByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
