package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.ReportFormat;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReportFormatTest {

    @Test
    public void testValues() {
        ReportFormat[] values = ReportFormat.values();
        assertEquals(3, values.length);
        assertEquals(ReportFormat.PDF, ReportFormat.valueOf("PDF"));
        assertEquals(ReportFormat.EXCEL, ReportFormat.valueOf("EXCEL"));
        assertEquals(ReportFormat.CSV, ReportFormat.valueOf("CSV"));
    }
}
