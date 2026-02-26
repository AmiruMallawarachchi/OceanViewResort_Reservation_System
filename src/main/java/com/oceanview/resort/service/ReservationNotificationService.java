package com.oceanview.resort.service;

import com.oceanview.resort.model.Reservation;

/**
 * Abstraction for publishing reservation-related notifications (confirmation, cancellation).
 * Implementations can use Kafka, email, or other channels. Supports Dependency Inversion
 * and testability by allowing injection of mock implementations.
 */
public interface ReservationNotificationService {

    /**
     * Publish a confirmation notification for a newly created reservation.
     */
    void publishConfirmation(Reservation reservation);

    /**
     * Publish a cancellation notification when a reservation is cancelled.
     */
    void publishCancellation(Reservation reservation);
}
