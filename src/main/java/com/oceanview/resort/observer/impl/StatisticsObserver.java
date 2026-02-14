package com.oceanview.resort.observer.impl;

import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.ReservationStatus;
import com.oceanview.resort.observer.ReservationEvent;
import com.oceanview.resort.observer.ReservationEventType;
import com.oceanview.resort.observer.ReservationObserver;

import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Observer that tracks reservation statistics.
 * Maintains counters for different types of reservation events.
 * 
 * This observer demonstrates how the Observer Pattern allows adding new
 * functionality (statistics tracking) without modifying existing code.
 * 
 * In a production system, this could update a database table or cache
 * with real-time statistics.
 */
public class StatisticsObserver implements ReservationObserver {
    
    private static final Logger logger = Logger.getLogger(StatisticsObserver.class.getName());
    
    // Statistics counters (using AtomicLong for thread safety)
    private final AtomicLong totalCreated = new AtomicLong(0);
    private final AtomicLong totalConfirmed = new AtomicLong(0);
    private final AtomicLong totalCancelled = new AtomicLong(0);
    private final AtomicLong totalCheckedIn = new AtomicLong(0);
    private final AtomicLong totalCheckedOut = new AtomicLong(0);
    
    @Override
    public void onReservationEvent(ReservationEvent event) {
        if (event == null || event.getReservation() == null) {
            return;
        }
        
        ReservationEventType eventType = event.getEventType();
        Reservation reservation = event.getReservation();
        
        // Update statistics based on event type
        switch (eventType) {
            case CREATED:
                totalCreated.incrementAndGet();
                logger.fine("Statistics: Reservation created. Total created: " + totalCreated.get());
                break;
            case CONFIRMED:
                totalConfirmed.incrementAndGet();
                logger.fine("Statistics: Reservation confirmed. Total confirmed: " + totalConfirmed.get());
                break;
            case CANCELLED:
                totalCancelled.incrementAndGet();
                logger.fine("Statistics: Reservation cancelled. Total cancelled: " + totalCancelled.get());
                break;
            case CHECKED_IN:
                totalCheckedIn.incrementAndGet();
                logger.fine("Statistics: Guest checked in. Total check-ins: " + totalCheckedIn.get());
                break;
            case CHECKED_OUT:
                totalCheckedOut.incrementAndGet();
                logger.fine("Statistics: Guest checked out. Total check-outs: " + totalCheckedOut.get());
                break;
            case UPDATED:
                // Track status changes from updates
                if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                    totalConfirmed.incrementAndGet();
                } else if (reservation.getStatus() == ReservationStatus.CANCELLED) {
                    totalCancelled.incrementAndGet();
                }
                break;
        }
    }
    
    /**
     * Get the total number of reservations created.
     */
    public long getTotalCreated() {
        return totalCreated.get();
    }
    
    /**
     * Get the total number of reservations confirmed.
     */
    public long getTotalConfirmed() {
        return totalConfirmed.get();
    }
    
    /**
     * Get the total number of reservations cancelled.
     */
    public long getTotalCancelled() {
        return totalCancelled.get();
    }
    
    /**
     * Get the total number of check-ins.
     */
    public long getTotalCheckedIn() {
        return totalCheckedIn.get();
    }
    
    /**
     * Get the total number of check-outs.
     */
    public long getTotalCheckedOut() {
        return totalCheckedOut.get();
    }
    
    /**
     * Get a summary of all statistics.
     */
    public String getStatisticsSummary() {
        return String.format(
            "Reservation Statistics - Created: %d, Confirmed: %d, Cancelled: %d, Checked In: %d, Checked Out: %d",
            totalCreated.get(), totalConfirmed.get(), totalCancelled.get(), 
            totalCheckedIn.get(), totalCheckedOut.get()
        );
    }
}
