package com.oceanview.resort.observer;

/**
 * Observer interface for reservation events.
 * Classes implementing this interface will be notified when reservation events occur.
 * 
 * This follows the Observer Pattern: observers register with a subject and receive
 * notifications when the subject's state changes.
 */
public interface ReservationObserver {
    
    /**
     * Called when a reservation event occurs.
     * 
     * @param event The reservation event containing details about what happened
     */
    void onReservationEvent(ReservationEvent event);
    
    /**
     * Optional: Get the observer's name for logging/debugging purposes.
     * 
     * @return A descriptive name for this observer
     */
    default String getObserverName() {
        return this.getClass().getSimpleName();
    }
}
