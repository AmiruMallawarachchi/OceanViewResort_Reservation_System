package com.oceanview.resort.observer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Subject class that manages observers and notifies them of reservation events.
 * This implements the Observer Pattern's Subject role:
 * - Maintains a list of observers
 * - Provides methods to attach/detach observers
 */
public class ReservationSubject {
    
    private final List<ReservationObserver> observers;
    
    public ReservationSubject() {
        // Use CopyOnWriteArrayList for thread safety
        this.observers = new CopyOnWriteArrayList<>();
    }

    public void attach(ReservationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(ReservationObserver observer) {
        observers.remove(observer);
    }

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

    public int getObserverCount() {
        return observers.size();
    }
    

    public boolean hasObservers() {
        return !observers.isEmpty();
    }
}
