package com.oceanview.resort.service.impl;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.messaging.KafkaEventProducer;
import com.oceanview.resort.messaging.ReservationEmailEvent;
import com.oceanview.resort.messaging.ReservationEmailEventType;
import com.oceanview.resort.model.Guest;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.Room;
import com.oceanview.resort.service.ReservationNotificationService;
import com.oceanview.resort.util.DateUtil;

import javax.annotation.Nonnull;

/**
 * Default implementation of {@link ReservationNotificationService} that publishes
 * reservation events via Kafka (when enabled) and falls back to direct email when Kafka is disabled.
 */
public class ReservationNotificationServiceImpl implements ReservationNotificationService {

    private final EmailService emailService;

    public ReservationNotificationServiceImpl() {
        this(new EmailService());
    }

    public ReservationNotificationServiceImpl(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void publishConfirmation(Reservation reservation) {
        ReservationEmailEvent event = buildEvent(reservation, ReservationEmailEventType.CONFIRMATION);
        KafkaEventProducer.sendReservationEmailEvent(event);

        if (!AppConfig.getBoolean("kafka.enabled", false)) {
            emailService.sendReservationEmail(event);
        }
    }

    @Override
    public void publishCancellation(Reservation reservation) {
        ReservationEmailEvent event = buildEvent(reservation, ReservationEmailEventType.CANCELLATION);
        event.setStatus("CANCELLED");
        KafkaEventProducer.sendReservationEmailEvent(event);

        if (!AppConfig.getBoolean("kafka.enabled", false)) {
            emailService.sendReservationEmail(event);
        }
    }

    private ReservationEmailEvent buildEvent(Reservation reservation, ReservationEmailEventType type) {
        return getReservationEmailEvent(reservation, type);
    }

    @Nonnull
    public static ReservationEmailEvent getReservationEmailEvent(Reservation reservation, ReservationEmailEventType type) {
        ReservationEmailEvent event = new ReservationEmailEvent();
        if (reservation == null) {
            event.setType(type.name());
            return event;
        }
        Guest guest = reservation.getGuest();
        Room room = reservation.getRoom();

        event.setType(type.name());
        event.setReservationId(reservation.getId());
        event.setReservationNo(reservation.getReservationNo());
        event.setGuestName(guest == null ? null : guest.getFullName());
        event.setGuestEmail(guest == null ? null : guest.getEmail());
        event.setRoomNumber(room == null ? null : room.getRoomNumber());
        event.setRoomTypeName(room == null || room.getRoomType() == null ? null : room.getRoomType().getTypeName());
        event.setCheckInDate(DateUtil.formatDate(reservation.getCheckInDate()));
        event.setCheckOutDate(DateUtil.formatDate(reservation.getCheckOutDate()));
        event.setStatus(reservation.getStatus() == null ? null : reservation.getStatus().name());
        return event;
    }
}
