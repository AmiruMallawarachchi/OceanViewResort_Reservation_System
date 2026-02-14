package com.oceanview.resort.dtoTest;

import com.oceanview.resort.dto.ReportDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ReportDTO getters and setters.
 */
public class ReportDTOTest {

    private ReportDTO dto;

    @Before
    public void setup() {
        dto = new ReportDTO();
    }

    @Test
    public void testDefaultValues() {
        Assert.assertEquals(0L, dto.getId());
        Assert.assertNull(dto.getReportType());
        Assert.assertNull(dto.getFormat());
        Assert.assertNull(dto.getGeneratedBy());
        Assert.assertNull(dto.getGeneratedAt());
        Assert.assertNull(dto.getParameters());
        Assert.assertNull(dto.getPeriod());
        Assert.assertNull(dto.getStartDate());
        Assert.assertNull(dto.getEndDate());
    }

    @Test
    public void testSetAndGetAllFields() {
        dto.setId(1L);
        dto.setReportType("OCCUPANCY");
        dto.setFormat("PDF");
        dto.setGeneratedBy("admin");
        dto.setGeneratedAt("2026-02-12 14:00");
        dto.setParameters("Period=DAILY");
        dto.setPeriod("DAILY");
        dto.setStartDate("2026-02-12");
        dto.setEndDate("2026-02-12");

        Assert.assertEquals(1L, dto.getId());
        Assert.assertEquals("OCCUPANCY", dto.getReportType());
        Assert.assertEquals("PDF", dto.getFormat());
        Assert.assertEquals("admin", dto.getGeneratedBy());
        Assert.assertEquals("2026-02-12 14:00", dto.getGeneratedAt());
        Assert.assertEquals("Period=DAILY", dto.getParameters());
        Assert.assertEquals("DAILY", dto.getPeriod());
        Assert.assertEquals("2026-02-12", dto.getStartDate());
        Assert.assertEquals("2026-02-12", dto.getEndDate());
    }
}
