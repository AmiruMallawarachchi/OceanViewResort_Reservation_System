package com.oceanview.resort.observerTest.impl;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.impl.StatisticsObserver;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StatisticsObserverTest {

    private StatisticsObserver observer;

    @Before
    public void setUp() {
        observer = new StatisticsObserver();
    }

    @Test
    public void testInitialCountersAreZero() {
        assertEquals(0, observer.getTotalCreated());
        assertEquals(0, observer.getTotalConfirmed());
        assertEquals(0, observer.getTotalCancelled());
        assertEquals(0, observer.getTotalCheckedIn());
        assertEquals(0, observer.getTotalCheckedOut());
    }

    @Test
    public void testOnReservationEvent_nullEvent_doesNotThrow() {
        observer.onReservationEvent(null);
        assertEquals(0, observer.getTotalCreated());
    }

    @Test
    public void testOnReservationEvent_nullReservation_doesNotThrow() {
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, null);
        observer.onReservationEvent(event);
        assertEquals(0, observer.getTotalCreated());
    }

    @Test
    public void testOnReservationEvent_created_incrementsCreated() {
        Reservation r = new Reservation();
        r.setReservationNo("RES-1");
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CREATED, r));
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CREATED, r));
        assertEquals(2, observer.getTotalCreated());
    }

    @Test
    public void testOnReservationEvent_confirmed_incrementsConfirmed() {
        Reservation r = new Reservation();
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CONFIRMED, r));
        assertEquals(1, observer.getTotalConfirmed());
    }

    @Test
    public void testOnReservationEvent_cancelled_incrementsCancelled() {
        Reservation r = new Reservation();
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CANCELLED, r));
        assertEquals(1, observer.getTotalCancelled());
    }

    @Test
    public void testOnReservationEvent_checkedIn_incrementsCheckedIn() {
        Reservation r = new Reservation();
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CHECKED_IN, r));
        assertEquals(1, observer.getTotalCheckedIn());
    }

    @Test
    public void testOnReservationEvent_checkedOut_incrementsCheckedOut() {
        Reservation r = new Reservation();
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CHECKED_OUT, r));
        assertEquals(1, observer.getTotalCheckedOut());
    }

    @Test
    public void testOnReservationEvent_updated_withConfirmedStatus_incrementsConfirmed() {
        Reservation r = new Reservation();
        r.setStatus(ReservationStatus.CONFIRMED);
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.UPDATED, r));
        assertEquals(1, observer.getTotalConfirmed());
    }

    @Test
    public void testOnReservationEvent_updated_withCancelledStatus_incrementsCancelled() {
        Reservation r = new Reservation();
        r.setStatus(ReservationStatus.CANCELLED);
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.UPDATED, r));
        assertEquals(1, observer.getTotalCancelled());
    }

    @Test
    public void testGetStatisticsSummary() {
        Reservation r = new Reservation();
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CREATED, r));
        observer.onReservationEvent(new ReservationEvent(ReservationEventType.CONFIRMED, r));
        String summary = observer.getStatisticsSummary();
        assertTrue(summary.contains("Created: 1"));
        assertTrue(summary.contains("Confirmed: 1"));
    }
}
