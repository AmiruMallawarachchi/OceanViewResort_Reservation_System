package com.oceanview.resort.strategy.impl;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.model.enums.GuestType;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.DiscountCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for calculating discounts based on guest type (REGULAR, VIP, CORPORATE).
 * 
 * This strategy:
 * - Finds all active GUEST_TYPE discounts
 * - Matches the guest's type with applicable discounts
 * - Returns the maximum discount percentage (if multiple discounts match)
 * 
 * Example: VIP guests get 15% discount, Corporate guests get 10% discount.
 */
public class GuestTypeDiscountStrategy implements DiscountCalculationStrategy {
    
    @Override
    public BigDecimal calculateDiscount(Reservation reservation, List<Discount> activeDiscounts, 
                                       DiscountCalculationContext context) {
        if (reservation == null || reservation.getGuest() == null) {
            return BigDecimal.ZERO;
        }
        
        GuestType guestType = reservation.getGuest().getGuestType();
        if (guestType == null) {
            return BigDecimal.ZERO;
        }
        
        // Find the maximum discount percentage for this guest type
        BigDecimal maxDiscount = activeDiscounts.stream()
                .filter(discount -> discount.getDiscountType() == DiscountType.GUEST_TYPE)
                .filter(discount -> discount.isActive())
                .filter(discount -> guestType.equals(discount.getGuestType()))
                .map(Discount::getPercent)
                .filter(percent -> percent != null && percent.compareTo(BigDecimal.ZERO) > 0)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        return maxDiscount;
    }
    
    @Override
    public DiscountType getDiscountType() {
        return DiscountType.GUEST_TYPE;
    }
}
