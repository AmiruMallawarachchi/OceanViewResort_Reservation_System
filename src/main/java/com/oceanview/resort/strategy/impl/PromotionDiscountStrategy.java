package com.oceanview.resort.strategy.impl;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.model.enums.DiscountType;
import com.oceanview.resort.strategy.DiscountCalculationContext;
import com.oceanview.resort.strategy.DiscountCalculationStrategy;

import java.math.BigDecimal;
import java.util.List;

/**
 * Strategy for calculating discounts based on selected promotion codes.
 */
public class PromotionDiscountStrategy implements DiscountCalculationStrategy {
    
    @Override
    public BigDecimal calculateDiscount(Reservation reservation, List<Discount> activeDiscounts, 
                                       DiscountCalculationContext context) {
        if (context == null || context.getSelectedPromotionIds() == null || 
            context.getSelectedPromotionIds().isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalPromotionDiscount = BigDecimal.ZERO;
        
        // Sum up discounts for all selected promotions
        for (Discount discount : activeDiscounts) {
            if (discount.getDiscountType() == DiscountType.PROMOTION && 
                discount.isActive() &&
                context.getSelectedPromotionIds().contains(discount.getId())) {
                
                if (discount.getPercent() != null && discount.getPercent().compareTo(BigDecimal.ZERO) > 0) {
                    totalPromotionDiscount = totalPromotionDiscount.add(discount.getPercent());
                }
            }
        }
        
        return totalPromotionDiscount;
    }
    
    @Override
    public DiscountType getDiscountType() {
        return DiscountType.PROMOTION;
    }
}
