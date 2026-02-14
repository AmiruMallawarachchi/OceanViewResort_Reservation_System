package com.oceanview.resort.observerTest;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.ReservationObserver;
import com.oceanview.resort.observer.ReservationSubject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReservationSubjectTest {

    private ReservationSubject subject;

    @Before
    public void setUp() {
        subject = new ReservationSubject();
    }

    @Test
    public void testInitialState_noObservers() {
        assertEquals(0, subject.getObserverCount());
        assertFalse(subject.hasObservers());
    }

    @Test
    public void testAttach_addsObserver() {
        ReservationObserver observer = mock(ReservationObserver.class);
        when(observer.getObserverName()).thenReturn("TestObserver");

        subject.attach(observer);

        assertEquals(1, subject.getObserverCount());
        assertTrue(subject.hasObservers());
    }

    @Test
    public void testAttach_null_doesNotAdd() {
        subject.attach(null);
        assertEquals(0, subject.getObserverCount());
    }

    @Test
    public void testAttach_duplicate_doesNotAddTwice() {
        ReservationObserver observer = mock(ReservationObserver.class);
        when(observer.getObserverName()).thenReturn("TestObserver");
        subject.attach(observer);
        subject.attach(observer);
        assertEquals(1, subject.getObserverCount());
    }

    @Test
    public void testDetach_removesObserver() {
        ReservationObserver observer = mock(ReservationObserver.class);
        when(observer.getObserverName()).thenReturn("TestObserver");
        subject.attach(observer);
        subject.detach(observer);
        assertEquals(0, subject.getObserverCount());
    }

    @Test
    public void testNotifyObservers_notifiesAllObservers() {
        ReservationObserver o1 = mock(ReservationObserver.class);
        ReservationObserver o2 = mock(ReservationObserver.class);
        when(o1.getObserverName()).thenReturn("O1");
        when(o2.getObserverName()).thenReturn("O2");
        subject.attach(o1);
        subject.attach(o2);

        Reservation reservation = new Reservation();
        reservation.setReservationNo("RES-1");
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, reservation);

        subject.notifyObservers(event);

        verify(o1).onReservationEvent(event);
        verify(o2).onReservationEvent(event);
    }

    @Test
    public void testNotifyObservers_nullEvent_doesNotNotify() {
        ReservationObserver observer = mock(ReservationObserver.class);
        when(observer.getObserverName()).thenReturn("O1");
        subject.attach(observer);
        subject.notifyObservers(null);
        verify(observer, never()).onReservationEvent(any());
    }
}
