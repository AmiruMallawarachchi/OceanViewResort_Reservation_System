package com.oceanview.resort.exceptionTest;

import com.oceanview.resort.exception.ReservationException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReservationExceptionTest {

    @Test
    public void testMessage() {
        ReservationException e = new ReservationException("Reservation not found");
        assertEquals("Reservation not found", e.getMessage());
    }

    @Test
    public void testIsRuntimeException() {
        assertTrue(new ReservationException("msg") instanceof RuntimeException);
    }
}
