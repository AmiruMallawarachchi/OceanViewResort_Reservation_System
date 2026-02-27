package com.oceanview.resort.utilTest;

import com.oceanview.resort.util.ReportExportUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReportExportUtilTest {

    @Test
    public void testGeneratePdf_returnsNonEmptyBytes() {
        List<String[]> rows = Arrays.asList(
                new String[]{"Total Rooms", "50"},
                new String[]{"Available", "30"}
        );
        byte[] result = ReportExportUtil.generatePdf("Occupancy Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void testGeneratePdf_emptyRows_returnsNonEmptyBytes() {
        List<String[]> rows = Collections.emptyList();
        byte[] result = ReportExportUtil.generatePdf("Empty Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void testGenerateExcel_returnsNonEmptyBytes() {
        List<String[]> rows = Arrays.asList(
                new String[]{"Metric", "Value"},
                new String[]{"Revenue", "10000"}
        );
        byte[] result = ReportExportUtil.generateExcel("Revenue Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }

    @Test
    public void testGenerateExcel_emptyRows_returnsNonEmptyBytes() {
        List<String[]> rows = Collections.emptyList();
        byte[] result = ReportExportUtil.generateExcel("Empty Report", rows);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.length > 0);
    }
}
