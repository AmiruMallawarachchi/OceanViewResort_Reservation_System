package com.oceanview.resort.serviceTest;

import com.oceanview.resort.model.enums.ReportFormat;
import com.oceanview.resort.service.ReportExporter;
import com.oceanview.resort.service.impl.ExcelReportExporter;
import com.oceanview.resort.service.impl.PdfReportExporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for ReportExporter interface contract (via PdfReportExporter and ExcelReportExporter).
 */
public class ReportExporterTest {

    private ReportExporter pdfExporter;
    private ReportExporter excelExporter;

    @Before
    public void setup() {
        pdfExporter = new PdfReportExporter();
        excelExporter = new ExcelReportExporter();
    }

    @Test
    public void getFormatKey_pdf_returnsPdf() {
        Assert.assertEquals(ReportFormat.PDF.name(), pdfExporter.getFormatKey());
    }

    @Test
    public void getFormatKey_excel_returnsExcel() {
        Assert.assertEquals(ReportFormat.EXCEL.name(), excelExporter.getFormatKey());
    }

    @Test
    public void export_pdf_returnsNonEmptyBytes() {
        List<String[]> rows = Arrays.asList(
                new String[]{"Metric1", "Value1"},
                new String[]{"Metric2", "Value2"}
        );
        byte[] result = pdfExporter.export("Test Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void export_excel_returnsNonEmptyBytes() {
        List<String[]> rows = Arrays.asList(
                new String[]{"Metric1", "Value1"},
                new String[]{"Metric2", "Value2"}
        );
        byte[] result = excelExporter.export("Test Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void export_emptyRows_returnsNonEmptyBytes() {
        List<String[]> rows = Collections.emptyList();

        byte[] pdfResult = pdfExporter.export("Empty Report", rows);
        Assert.assertNotNull(pdfResult);
        Assert.assertTrue(pdfResult.length > 0);

        byte[] excelResult = excelExporter.export("Empty Report", rows);
        Assert.assertNotNull(excelResult);
        Assert.assertTrue(excelResult.length > 0);
    }
}
