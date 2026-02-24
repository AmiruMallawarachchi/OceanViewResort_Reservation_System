package com.oceanview.resort.observerTest.impl;

import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.impl.AuditLogObserver;
import org.junit.Before;
import org.junit.Test;

public class AuditLogObserverTest {

    private AuditLogObserver observer;

    @Before
    public void setUp() {
        observer = new AuditLogObserver();
    }

    @Test
    public void testOnReservationEvent_nullEvent_doesNotThrow() {
        observer.onReservationEvent(null);
    }

    @Test
    public void testOnReservationEvent_nullReservation_doesNotThrow() {
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, null);
        observer.onReservationEvent(event);
    }

    @Test
    public void testOnReservationEvent_validEvent_doesNotThrow() {
        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES-001");
        reservation.setStatus(com.oceanview.resort.model.enums.ReservationStatus.CONFIRMED);
        Guest guest = new Guest();
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setEmail("john@example.com");
        reservation.setGuest(guest);
        Room room = new Room();
        room.setRoomNumber("101");
        reservation.setRoom(room);
        reservation.setCheckInDate(java.time.LocalDate.of(2026, 2, 14));
        reservation.setCheckOutDate(java.time.LocalDate.of(2026, 2, 16));

        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, reservation);
        observer.onReservationEvent(event);
    }
}
