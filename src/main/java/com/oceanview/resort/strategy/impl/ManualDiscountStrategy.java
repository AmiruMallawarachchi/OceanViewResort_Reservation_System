package com.oceanview.resort.strategy.impl;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.DiscountCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for applying manual discounts entered by staff.
 * This strategy:
 * - Retrieves the manual discount percentage from the context
 * - Returns it directly (no validation against active discounts)
 * Example: Staff member manually applies a 20% discount for special circumstances.
 */
public class ManualDiscountStrategy implements DiscountCalculationStrategy {
    
    @Override
    public BigDecimal calculateDiscount(Reservation reservation, List<Discount> activeDiscounts, 
                                       DiscountCalculationContext context) {
        if (context == null || context.getManualDiscountPercent() == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal manualDiscount = context.getManualDiscountPercent();
        
        // Ensure manual discount is non-negative
        if (manualDiscount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        
        return manualDiscount;
    }
    
    @Override
    public DiscountType getDiscountType() {
        // Manual discount doesn't have a DiscountType enum value
        // Return null or handle differently - for now, we'll use a workaround
        return null; // Manual discounts are not stored in the Discount table
    }
    
    @Override
    public String getStrategyName() {
        return "ManualDiscountStrategy";
    }
}
