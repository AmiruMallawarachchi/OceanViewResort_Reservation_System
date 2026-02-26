package com.oceanview.resort.UtilTest;

import com.oceanview.resort.util.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Tests for DateUtil (parseDate, formatDate, formatDateTime).
 */
public class DateUtilTest {

    @Test
    public void parseDate_nullReturnsNull() {
        Assert.assertNull(DateUtil.parseDate(null));
    }

    @Test
    public void parseDate_blankReturnsNull() {
        Assert.assertNull(DateUtil.parseDate(""));
        Assert.assertNull(DateUtil.parseDate("   "));
    }

    @Test
    public void parseDate_validReturnsLocalDate() {
        LocalDate d = DateUtil.parseDate("2026-02-12");
        Assert.assertNotNull(d);
        Assert.assertEquals(2026, d.getYear());
        Assert.assertEquals(2, d.getMonthValue());
        Assert.assertEquals(12, d.getDayOfMonth());
    }

    @Test(expected = Exception.class)
    public void parseDate_invalidThrows() {
        DateUtil.parseDate("not-a-date");
    }

    @Test
    public void formatDate_nullReturnsNull() {
        Assert.assertNull(DateUtil.formatDate(null));
    }

    @Test
    public void formatDate_validReturnsIsoString() {
        LocalDate d = LocalDate.of(2026, 2, 12);
        Assert.assertEquals("2026-02-12", DateUtil.formatDate(d));
    }

    @Test
    public void formatDateTime_nullReturnsNull() {
        Assert.assertNull(DateUtil.formatDateTime(null));
    }

    @Test
    public void formatDateTime_validReturnsFormattedString() {
        LocalDateTime dt = LocalDateTime.of(2026, 2, 12, 14, 30);
        Assert.assertEquals("2026-02-12 14:30", DateUtil.formatDateTime(dt));
    }
}
