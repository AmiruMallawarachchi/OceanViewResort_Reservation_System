package com.oceanview.resort.messagingTest;

import com.oceanview.resort.messaging.ReservationEmailEventType;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for ReservationEmailEventType enum.
 */
public class ReservationEmailEventTypeTest {

    @Test
    public void testEnumValues() {
        ReservationEmailEventType[] values = ReservationEmailEventType.values();
        Assert.assertEquals(2, values.length);
        Assert.assertEquals(ReservationEmailEventType.CONFIRMATION, values[0]);
        Assert.assertEquals(ReservationEmailEventType.CANCELLATION, values[1]);
    }

    @Test
    public void testValueOf() {
        Assert.assertEquals(ReservationEmailEventType.CONFIRMATION, ReservationEmailEventType.valueOf("CONFIRMATION"));
        Assert.assertEquals(ReservationEmailEventType.CANCELLATION, ReservationEmailEventType.valueOf("CANCELLATION"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValueOfInvalid() {
        ReservationEmailEventType.valueOf("INVALID");
    }
}
