package com.oceanview.resort.mapperTest;

import com.oceanview.resort.dto.ReportDTO;
import com.oceanview.resort.mapper.ReportMapper;
import com.oceanview.resort.model.Report;
import com.oceanview.resort.model.User;
import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.model.enums.ReportType;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

/**
 * Tests for ReportMapper (Report -> ReportDTO).
 */
public class ReportMapperTest {

    @Test
    public void toDTO_nullReturnsNull() {
        Assert.assertNull(ReportMapper.toDTO(null));
    }

    @Test
    public void toDTO_mapsAllFields() {
        Report report = new Report();
        report.setId(1L);
        report.setReportType(ReportType.OCCUPANCY);
        report.setFormat(ReportFormat.PDF);
        report.setParameters("Period=DAILY");
        report.setGeneratedAt(LocalDateTime.of(2026, 2, 12, 14, 0));
        User user = new User();
        user.setUsername("admin");
        report.setGeneratedBy(user);

        ReportDTO dto = ReportMapper.toDTO(report);

        Assert.assertNotNull(dto);
        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("OCCUPANCY", dto.getReportType());
        Assert.assertEquals("PDF", dto.getFormat());
        Assert.assertEquals("admin", dto.getGeneratedBy());
        Assert.assertEquals("2026-02-12 14:00", dto.getGeneratedAt());
        Assert.assertEquals("Period=DAILY", dto.getParameters());
    }

    @Test
    public void toDTO_nullEnums_mapsToNull() {
        Report report = new Report();
        report.setId(2L);
        report.setReportType(null);
        report.setFormat(null);
        report.setGeneratedBy(null);
        report.setGeneratedAt(null);

        ReportDTO dto = ReportMapper.toDTO(report);

        Assert.assertNull(dto.getReportType());
        Assert.assertNull(dto.getFormat());
        Assert.assertNull(dto.getGeneratedBy());
        Assert.assertNull(dto.getGeneratedAt());
    }
}
