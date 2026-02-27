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

        return activeDiscounts.stream()
                .filter(discount -> discount.getDiscountType() == DiscountType.GUEST_TYPE)
                .filter(Discount::isActive)
                .filter(discount -> guestType.equals(discount.getGuestType()))
                .map(Discount::getPercent)
                .filter(percent -> percent != null && percent.compareTo(BigDecimal.ZERO) > 0)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
    
    @Override
    public DiscountType getDiscountType() {
        return DiscountType.GUEST_TYPE;
    }
}
