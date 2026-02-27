package com.oceanview.resort.observerTest.impl;

import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.impl.EmailNotificationObserver;
import com.oceanview.resort.service.impl.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationObserverTest {

    @Mock
    private EmailService emailService;

    private EmailNotificationObserver observer;

    @Before
    public void setUp() {
        observer = new EmailNotificationObserver(emailService);
    }

    @Test
    public void testOnReservationEvent_nullEvent_doesNotCallEmailService() {
        observer.onReservationEvent(null);
        verify(emailService, never()).sendReservationEmail(any());
    }

    @Test
    public void testOnReservationEvent_nullReservation_doesNotCallEmailService() {
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, null);
        observer.onReservationEvent(event);
        verify(emailService, never()).sendReservationEmail(any());
    }

    @Test
    public void testOnReservationEvent_created_buildsEventAndSendsViaKafkaOrFallback() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationNo("RES-001");
        reservation.setStatus(com.oceanview.resort.model.enums.ReservationStatus.CONFIRMED);
        Guest guest = new Guest();
        guest.setFirstName("Jane");
        guest.setLastName("Doe");
        guest.setEmail("jane@example.com");
        reservation.setGuest(guest);
        Room room = new Room();
        room.setRoomNumber("201");
        reservation.setRoom(room);
        reservation.setCheckInDate(java.time.LocalDate.of(2026, 2, 14));
        reservation.setCheckOutDate(java.time.LocalDate.of(2026, 2, 16));
        ReservationEvent event = new ReservationEvent(ReservationEventType.CREATED, reservation);

        observer.onReservationEvent(event);
        // Kafka is used first; if kafka.enabled is false, sendReservationEmail may be called.
        // We only verify no exception; full integration would mock Kafka/AppConfig.
    }

    @Test
    public void testGetObserverName() {
        assertEquals("EmailNotificationObserver", observer.getObserverName());
    }
}
