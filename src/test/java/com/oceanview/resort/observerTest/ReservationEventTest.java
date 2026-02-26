package com.oceanview.resort.observerTest;

import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ReservationEventTest {

    @Test
    public void testConstructor_twoArgs_setsEventTypeAndReservation() {
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES-001");
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, reservation);

        assertEquals(ReservationEventType.CREATED, event.getEventType());
        assertEquals(reservation, event.getReservation());
        assertNull(event.getPreviousReservation());
        assertNotNull(event.getTimestamp());
    }

    @Test
    public void testConstructor_threeArgs_setsPreviousReservation() {
        Reservation current = new Reservation();
        current.setReservationNo("RES-001");
        Reservation previous = new Reservation();
        previous.setReservationNo("RES-001");
        ReservationEvent event = new ReservationEvent(ReservationEventType.UPDATED, current, previous);

        assertEquals(ReservationEventType.UPDATED, event.getEventType());
        assertEquals(current, event.getReservation());
        assertEquals(previous, event.getPreviousReservation());
        assertNotNull(event.getTimestamp());
    }

    @Test
    public void testSettersAndGetters() {
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES-002");
        ReservationEvent event = new ReservationEvent(ReservationEventType.CONFIRMED, reservation);

        event.setEventType(ReservationEventType.CANCELLED);
        event.setDescription("Guest requested cancellation");
        LocalDateTime customTime = LocalDateTime.of(2026, 2, 14, 10, 0);
        event.setTimestamp(customTime);

        assertEquals(ReservationEventType.CANCELLED, event.getEventType());
        assertEquals("Guest requested cancellation", event.getDescription());
        assertEquals(customTime, event.getTimestamp());
    }

    @Test
    public void testToString_includesEventTypeAndReservationNo() {
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES-123");
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, reservation);

        String str = event.toString();
        assertTrue(str.contains("CREATED"));
        assertTrue(str.contains("RES-123"));
    }

    @Test
    public void testToString_withNullReservation() {
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, null);
        String str = event.toString();
        assertTrue(str.contains("null"));
    }
}
