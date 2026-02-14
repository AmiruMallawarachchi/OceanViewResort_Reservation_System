package com.oceanview.resort.observer;

/**
 * Enumeration of reservation event types that can trigger observer notifications.
 */
public enum ReservationEventType {
    CREATED,
    CONFIRMED,
    CANCELLED,
    CHECKED_IN,
    CHECKED_OUT,
    UPDATED
}
