package com.oceanview.resort.modelTest.enums;

import com.oceanview.resort.model.enums.ReservationStatus;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReservationStatusTest {

    @Test
    public void testValues() {
        ReservationStatus[] values = ReservationStatus.values();
        assertEquals(5, values.length);
        assertEquals(ReservationStatus.PENDING, ReservationStatus.valueOf("PENDING"));
        assertEquals(ReservationStatus.CONFIRMED, ReservationStatus.valueOf("CONFIRMED"));
        assertEquals(ReservationStatus.CHECKED_IN, ReservationStatus.valueOf("CHECKED_IN"));
        assertEquals(ReservationStatus.CHECKED_OUT, ReservationStatus.valueOf("CHECKED_OUT"));
        assertEquals(ReservationStatus.CANCELLED, ReservationStatus.valueOf("CANCELLED"));
    }
}
