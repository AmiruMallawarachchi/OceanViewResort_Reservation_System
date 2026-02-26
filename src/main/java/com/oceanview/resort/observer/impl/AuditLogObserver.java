package com.oceanview.resort.observer.impl;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationObserver;

import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Observer that logs reservation events for audit purposes.
 * Records all reservation state changes to the application log.
 * This provides an audit trail of all reservation activities for compliance
 * and debugging purposes.
 */
public class AuditLogObserver implements ReservationObserver {
    
    private static final Logger logger = Logger.getLogger(AuditLogObserver.class.getName());
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void onReservationEvent(ReservationEvent event) {
        if (event == null || event.getReservation() == null) {
            return;
        }
        
        Reservation reservation = event.getReservation();
        String reservationNo = reservation.getReservationNo();
        String eventType = event.getEventType().name();
        String timestamp = event.getTimestamp().format(FORMATTER);
        
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("AUDIT: Reservation Event - ");
        logMessage.append("Type: ").append(eventType);
        logMessage.append(", ReservationNo: ").append(reservationNo);
        logMessage.append(", Timestamp: ").append(timestamp);
        
        if (reservation.getGuest() != null) {
            logMessage.append(", Guest: ").append(reservation.getGuest().getFullName());
            logMessage.append(" (").append(reservation.getGuest().getEmail()).append(")");
        }
        
        if (reservation.getRoom() != null) {
            logMessage.append(", Room: ").append(reservation.getRoom().getRoomNumber());
        }
        
        logMessage.append(", Status: ").append(reservation.getStatus());
        
        if (reservation.getCheckInDate() != null && reservation.getCheckOutDate() != null) {
            logMessage.append(", Dates: ").append(reservation.getCheckInDate())
                      .append(" to ").append(reservation.getCheckOutDate());
        }
        
        // Log status changes for update events
        if (event.getPreviousReservation() != null) {
            logMessage.append(", Previous Status: ").append(event.getPreviousReservation().getStatus());
        }
        
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            logMessage.append(", Description: ").append(event.getDescription());
        }
        
        logger.info(logMessage.toString());
    }
}
