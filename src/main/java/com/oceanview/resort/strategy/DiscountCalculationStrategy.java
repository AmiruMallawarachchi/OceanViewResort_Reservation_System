package com.oceanview.resort.strategy;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy interface for discount calculation algorithms.
 * 
 * The Strategy Pattern allows different discount calculation methods to be
 * encapsulated and made interchangeable. This makes it easy to add new discount
 * types without modifying existing code (Open/Closed Principle).
 * 
 * Each strategy calculates the discount percentage applicable to a reservation
 * based on its specific rules (guest type, promotion codes, manual discounts, etc.).
 */
public interface DiscountCalculationStrategy {
    
    /**
     * Calculate the discount percentage for a reservation based on this strategy's rules.
     * 
     * @param reservation The reservation to calculate discount for
     * @param activeDiscounts List of all active discounts in the system
     * @param context Additional context (e.g., selected promotion IDs, manual discount)
     * @return The discount percentage (0-100) applicable to this reservation
     */
    BigDecimal calculateDiscount(Reservation reservation, List<Discount> activeDiscounts, DiscountCalculationContext context);
    
    /**
     * Get the discount type this strategy handles.
     * 
     * @return The discount type enum value
     */
    com.oceanview.resort.model.enums.DiscountType getDiscountType();
    
    /**
     * Get a human-readable name for this strategy (for logging/debugging).
     * 
     * @return Strategy name
     */
    default String getStrategyName() {
        return this.getClass().getSimpleName();
    }
}
