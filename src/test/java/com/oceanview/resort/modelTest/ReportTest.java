package com.oceanview.resort.modelTest;

import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class ReportTest {

    private Report report;

    @Before
    public void setUp() {
        report = new Report();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, report.getId());
        Assert.assertNull(report.getReportType());
        Assert.assertNull(report.getFormat());
        Assert.assertNull(report.getGeneratedBy());
        Assert.assertNull(report.getGeneratedAt());
        Assert.assertNull(report.getParameters());
        Assert.assertNull(report.getContent());
    }

    @Test
    public void testGettersAndSetters() {
        User user = new User();
        LocalDateTime at = LocalDateTime.of(2026, 2, 12, 14, 0);
        byte[] content = new byte[]{1, 2, 3};

        report.setId(1L);
        report.setReportType(ReportType.OCCUPANCY);
        report.setFormat(ReportFormat.PDF);
        report.setGeneratedBy(user);
        report.setGeneratedAt(at);
        report.setParameters("Period=DAILY");
        report.setContent(content);

        Assert.assertEquals(1L, report.getId());
        Assert.assertEquals(ReportType.OCCUPANCY, report.getReportType());
        Assert.assertEquals(ReportFormat.PDF, report.getFormat());
        Assert.assertEquals(user, report.getGeneratedBy());
        Assert.assertEquals(at, report.getGeneratedAt());
        Assert.assertEquals("Period=DAILY", report.getParameters());
        Assert.assertArrayEquals(content, report.getContent());
    }

    @Test
    public void testConstructorWithArgs() {
        User user = new User();
        LocalDateTime at = LocalDateTime.now();
        byte[] content = new byte[]{4, 5, 6};
        Report r = new Report(2L, ReportType.REVENUE, ReportFormat.EXCEL, user, at, "params", content);
        Assert.assertEquals(2L, r.getId());
        Assert.assertEquals(ReportType.REVENUE, r.getReportType());
        Assert.assertEquals(ReportFormat.EXCEL, r.getFormat());
        Assert.assertArrayEquals(content, r.getContent());
    }
}
