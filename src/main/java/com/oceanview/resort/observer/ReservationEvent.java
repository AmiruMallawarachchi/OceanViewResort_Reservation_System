package com.oceanview.resort.observer;

import com.oceanview.resort.model.Reservation;
import java.time.LocalDateTime;

/**
 * Event object that encapsulates reservation state changes.
 * This is passed to observers when reservation events occur.
 */
public class ReservationEvent {
    private ReservationEventType eventType;
    private Reservation reservation;
    private Reservation previousReservation; // For update events, holds the previous state
    private LocalDateTime timestamp;
    private String description;

    public ReservationEvent(ReservationEventType eventType, Reservation reservation) {
        this.eventType = eventType;
        this.reservation = reservation;
        this.timestamp = LocalDateTime.now();
    }

    public ReservationEvent(ReservationEventType eventType, Reservation reservation, Reservation previousReservation) {
        this.eventType = eventType;
        this.reservation = reservation;
        this.previousReservation = previousReservation;
        this.timestamp = LocalDateTime.now();
    }

    public ReservationEventType getEventType() {
        return eventType;
    }

    public void setEventType(ReservationEventType eventType) {
        this.eventType = eventType;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Reservation getPreviousReservation() {
        return previousReservation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ReservationEvent{" +
                "eventType=" + eventType +
                ", reservationNo=" + (reservation != null ? reservation.getReservationNo() : "null") +
                ", timestamp=" + timestamp +
                ", description='" + description + '\'' +
                '}';
    }
}
