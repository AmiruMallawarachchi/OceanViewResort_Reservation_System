package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.ReportType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReportTypeTest {

    @Test
    public void testValues() {
        ReportType[] values = ReportType.values();
        assertEquals(7, values.length);
        assertEquals(ReportType.OCCUPANCY, ReportType.valueOf("OCCUPANCY"));
        assertEquals(ReportType.REVENUE, ReportType.valueOf("REVENUE"));
        assertEquals(ReportType.RESERVATION, ReportType.valueOf("RESERVATION"));
        assertEquals(ReportType.DAILY_ACTIVITY, ReportType.valueOf("DAILY_ACTIVITY"));
        assertEquals(ReportType.GUEST_SEGMENT, ReportType.valueOf("GUEST_SEGMENT"));
        assertEquals(ReportType.DISCOUNT_PERFORMANCE, ReportType.valueOf("DISCOUNT_PERFORMANCE"));
        assertEquals(ReportType.CANCELLATION, ReportType.valueOf("CANCELLATION"));
    }
}
