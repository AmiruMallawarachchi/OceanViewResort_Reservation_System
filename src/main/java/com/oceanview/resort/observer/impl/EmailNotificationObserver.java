package com.oceanview.resort.observer.impl;

import com.oceanview.resort.config.AppConfig;
import com.oceanview.resort.messaging.KafkaEventProducer;
import com.oceanview.resort.messaging.ReservationEmailEvent;
import com.oceanview.resort.messaging.ReservationEmailEventType;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.ReservationObserver;
import com.oceanview.resort.service.impl.EmailService;

import static com.oceanview.resort.service.impl.ReservationNotificationServiceImpl.getReservationEmailEvent;

/**
 * Observer that sends email notifications when reservation events occur.
 * Handles CONFIRMATION and CANCELLATION events by sending emails to guests.
 * This observer maintains backward compatibility with the existing email notification system.
 */
public class EmailNotificationObserver implements ReservationObserver {
    
    private final EmailService emailService;
    
    public EmailNotificationObserver() {
        this(new EmailService());
    }
    
    public EmailNotificationObserver(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Override
    public void onReservationEvent(ReservationEvent event) {
        if (event == null || event.getReservation() == null) {
            return;
        }
        
        ReservationEventType eventType = event.getEventType();
        Reservation reservation = event.getReservation();
        
        // Only send emails for confirmation and cancellation events
        if (eventType == ReservationEventType.CREATED || eventType == ReservationEventType.CONFIRMED) {
            sendConfirmationEmail(reservation);
        } else if (eventType == ReservationEventType.CANCELLED) {
            sendCancellationEmail(reservation);
        }
    }
    
    private void sendConfirmationEmail(Reservation reservation) {
        ReservationEmailEvent emailEvent = buildEmailEvent(reservation, ReservationEmailEventType.CONFIRMATION);
        KafkaEventProducer.sendReservationEmailEvent(emailEvent);
        
        // Fallback to direct email if Kafka is disabled
        if (!AppConfig.getBoolean("kafka.enabled", false)) {
            emailService.sendReservationEmail(emailEvent);
        }
    }
    
    private void sendCancellationEmail(Reservation reservation) {
        ReservationEmailEvent emailEvent = buildEmailEvent(reservation, ReservationEmailEventType.CANCELLATION);
        emailEvent.setStatus("CANCELLED");
        KafkaEventProducer.sendReservationEmailEvent(emailEvent);
        
        // Fallback to direct email if Kafka is disabled
        if (!AppConfig.getBoolean("kafka.enabled", false)) {
            emailService.sendReservationEmail(emailEvent);
        }
    }
    
    private ReservationEmailEvent buildEmailEvent(Reservation reservation, ReservationEmailEventType type) {
        return getReservationEmailEvent(reservation, type);
    }
}
