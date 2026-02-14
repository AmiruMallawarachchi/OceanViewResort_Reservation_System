package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.service.impl.ExcelReportExporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExcelReportExporterTest {

    private ExcelReportExporter exporter;

    @Before
    public void setup() {
        exporter = new ExcelReportExporter();
    }

    @Test
    public void testGetFormatKey_returnsExcel() {
        Assert.assertEquals(ReportFormat.EXCEL.name(), exporter.getFormatKey());
    }

    @Test
    public void testExport_returnsNonEmptyBytes() {
        List<String[]> rows = Arrays.asList(
                new String[]{"Metric1", "Value1"},
                new String[]{"Metric2", "Value2"}
        );
        byte[] result = exporter.export("Test Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void testExport_emptyRows_returnsNonEmptyBytes() {
        List<String[]> rows = Collections.emptyList();
        byte[] result = exporter.export("Empty Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }
}
