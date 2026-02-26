package com.oceanview.resort.repositoryTest;

import com.oceanview.resort.dao.ReportDAO;
import com.oceanview.resort.dao.UserDAO;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import com.oceanview.resort.repository.ReportRepository;
import com.oceanview.resort.security.PasswordUtil;
import com.oceanview.resort.model.enums.UserRole;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Tests ReportRepository contract using ReportDAO implementation.
 */
public class ReportRepositoryTest {

    private final ReportRepository repository = new ReportDAO();
    private final UserDAO userDAO = new UserDAO();
    private long createdReportId;
    private long createdUserId;
    private final String testUsername = "repo_report_" + System.currentTimeMillis();

    @Before
    public void setUp() {
        User user = new User();
        user.setUsername(testUsername);
        user.setPasswordHash(PasswordUtil.hashPassword("Pass123!"));
        user.setFullName("Report Repo Test");
        user.setEmail(testUsername + "@test.com");
        user.setRole(UserRole.RESERVATIONIST);
        user.setActive(true);
        createdUserId = userDAO.create(user).getId();
    }

    @After
    public void tearDown() {
        if (createdReportId > 0) {
            try {
                java.sql.Connection conn = com.oceanview.resort.config.DatabaseConnection.getConnection();
                try (java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM reports WHERE id = ?")) {
                    ps.setLong(1, createdReportId);
                    ps.executeUpdate();
                }
            } catch (Exception ignored) {
            }
        }
        if (createdUserId > 0) {
            try {
                userDAO.delete(createdUserId);
            } catch (Exception ignored) {
            }
        }
    }

    @Test
    public void testCreateFindByIdFindAll() {
        Report report = new Report();
        report.setReportType(ReportType.OCCUPANCY);
        report.setFormat(ReportFormat.PDF);
        report.setGeneratedBy(userDAO.findById(createdUserId));
        report.setParameters("REPO_TEST");
        report.setContent(new byte[]{1, 2, 3});

        Report created = repository.create(report);
        Assert.assertNotNull(created);
        Assert.assertTrue(created.getId() > 0);
        createdReportId = created.getId();

        Report found = repository.findById(createdReportId);
        Assert.assertNotNull(found);
        Assert.assertEquals(createdReportId, found.getId());
        Assert.assertEquals(ReportType.OCCUPANCY, found.getReportType());

        List<Report> all = repository.findAll();
        Assert.assertNotNull(all);
        Assert.assertTrue(all.stream().anyMatch(r -> r.getId() == createdReportId));
    }

    @Test
    public void testFindByIdNotFound() {
        Assert.assertNull(repository.findById(999999L));
    }
}
