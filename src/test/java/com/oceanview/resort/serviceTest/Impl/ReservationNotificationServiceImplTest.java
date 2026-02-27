package com.oceanview.resort.serviceTest.Impl;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.model.RoomType;
import com.oceanview.resort.service.impl.EmailService;
import com.oceanview.resort.service.impl.ReservationNotificationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReservationNotificationServiceImplTest {

    @Mock
    private EmailService emailService;

    private ReservationNotificationServiceImpl notificationService;

    @Before
    public void setup() throws Exception {
        notificationService = new ReservationNotificationServiceImpl(emailService);
        // Ensure kafka.enabled is false so email path is used
        Field propsField = AppConfig.class.getDeclaredField("PROPERTIES");
        propsField.setAccessible(true);
        Properties props = (Properties) propsField.get(null);
        props.setProperty("kafka.enabled", "false");
    }

    @Test
    public void testPublishConfirmation_buildsEventAndSendsEmailWhenKafkaDisabled() {
        Reservation res = buildReservation(1L, "RES-001");
        try (MockedStatic<com.oceanview.resort.messaging.KafkaEventProducer> kafka =
                     mockStatic(com.oceanview.resort.messaging.KafkaEventProducer.class)) {
            notificationService.publishConfirmation(res);
            kafka.verify(() -> com.oceanview.resort.messaging.KafkaEventProducer.sendReservationEmailEvent(any()));
            verify(emailService).sendReservationEmail(any());
        }
    }

    @Test
    public void testPublishCancellation_buildsEventWithCancelledStatus() {
        Reservation res = buildReservation(2L, "RES-002");
        try (MockedStatic<com.oceanview.resort.messaging.KafkaEventProducer> kafka =
                     mockStatic(com.oceanview.resort.messaging.KafkaEventProducer.class)) {
            notificationService.publishCancellation(res);
            kafka.verify(() -> com.oceanview.resort.messaging.KafkaEventProducer.sendReservationEmailEvent(any()));
            verify(emailService).sendReservationEmail(any());
        }
    }

    @Test
    public void testPublishConfirmation_nullReservation_stillSendsEvent() {
        try (MockedStatic<com.oceanview.resort.messaging.KafkaEventProducer> kafka =
                     mockStatic(com.oceanview.resort.messaging.KafkaEventProducer.class)) {
            notificationService.publishConfirmation(null);
            kafka.verify(() -> com.oceanview.resort.messaging.KafkaEventProducer.sendReservationEmailEvent(any()));
            verify(emailService).sendReservationEmail(any());
        }
    }

    private Reservation buildReservation(long id, String no) {
        Reservation res = new Reservation();
        res.setId(id);
        res.setReservationNo(no);
        res.setCheckInDate(LocalDate.of(2026, 2, 15));
        res.setCheckOutDate(LocalDate.of(2026, 2, 18));
        Guest guest = new Guest();
        guest.setFirstName("John");
        guest.setLastName("Doe");
        guest.setEmail("john@test.com");
        res.setGuest(guest);
        Room room = new Room();
        room.setRoomNumber("101");
        RoomType rt = new RoomType();
        rt.setTypeName("Deluxe");
        room.setRoomType(rt);
        res.setRoom(room);
        return res;
    }
}
