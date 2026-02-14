package com.oceanview.resort.observerTest;

import com.oceanview.resort.observer.ReservationEventType;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReservationEventTypeTest {

    @Test
    public void testValues() {
        ReservationEventType[] values = ReservationEventType.values();
        assertEquals(6, values.length);
        assertEquals(ReservationEventType.CREATED, values[0]);
        assertEquals(ReservationEventType.CONFIRMED, values[1]);
        assertEquals(ReservationEventType.CANCELLED, values[2]);
        assertEquals(ReservationEventType.CHECKED_IN, values[3]);
        assertEquals(ReservationEventType.CHECKED_OUT, values[4]);
        assertEquals(ReservationEventType.UPDATED, values[5]);
    }

    @Test
    public void testValueOf() {
        assertEquals(ReservationEventType.CREATED, ReservationEventType.valueOf("CREATED"));
        assertEquals(ReservationEventType.CANCELLED, ReservationEventType.valueOf("CANCELLED"));
    }
}
