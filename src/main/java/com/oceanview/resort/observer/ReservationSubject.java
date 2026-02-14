package com.oceanview.resort.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subject class that manages observers and notifies them of reservation events.
 * 
 * This implements the Observer Pattern's Subject role:
 * - Maintains a list of observers
 * - Provides methods to attach/detach observers
 * - Notifies all observers when events occur
 * 
 * Thread-safe implementation using CopyOnWriteArrayList for concurrent access.
 */
public class ReservationSubject {
    
    private final List<ReservationObserver> observers;
    
    public ReservationSubject() {
        // Use CopyOnWriteArrayList for thread safety
        this.observers = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Attach an observer to receive reservation event notifications.
     * 
     * @param observer The observer to attach
     */
    public void attach(ReservationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Detach an observer so it no longer receives notifications.
     * 
     * @param observer The observer to detach
     */
    public void detach(ReservationObserver observer) {
        observers.remove(observer);
    }
    
    /**
     * Notify all attached observers of a reservation event.
     * 
     * @param event The reservation event to notify observers about
     */
    public void notifyObservers(ReservationEvent event) {
        if (event == null) {
            return;
        }
        
        for (ReservationObserver observer : observers) {
            try {
                observer.onReservationEvent(event);
            } catch (Exception e) {
                // Log error but don't stop other observers from being notified
                System.err.println("Error notifying observer " + observer.getObserverName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the number of attached observers.
     * 
     * @return The count of observers
     */
    public int getObserverCount() {
        return observers.size();
    }
    
    /**
     * Check if any observers are attached.
     * 
     * @return true if there are observers, false otherwise
     */
    public boolean hasObservers() {
        return !observers.isEmpty();
    }
}
