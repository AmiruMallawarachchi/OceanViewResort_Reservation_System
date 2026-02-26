package com.oceanview.resort.service;

import com.oceanview.resort.model.Reservation;

/**
 * Abstraction for publishing reservation-related notifications (confirmation, cancellation).
 * Implementations can use Kafka, email, or other channels. Supports Dependency Inversion
 * and testability by allowing injection of mock implementations.
 */
public interface ReservationNotificationService {

    void publishConfirmation(Reservation reservation);

    void publishCancellation(Reservation reservation);
}
