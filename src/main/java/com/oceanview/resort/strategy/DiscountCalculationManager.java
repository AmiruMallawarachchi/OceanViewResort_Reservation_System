package com.oceanview.resort.strategy;

import com.oceanview.resort.model.Discount;
import com.oceanview.resort.model.Reservation;
import com.oceanview.resort.repository.DiscountRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Manager class that coordinates multiple discount calculation strategies.
 * 
 * This class implements the Strategy Pattern by:
 * - Maintaining a list of discount calculation strategies
 * - Executing each strategy and combining their results
 * - Applying business rules (e.g., maximum 100% discount)
 * 
 * This follows the Strategy Pattern's Context role, which uses strategies
 * to perform calculations without knowing their specific implementations.
 */
public class DiscountCalculationManager {
    
    private final List<DiscountCalculationStrategy> strategies;
    private final DiscountRepository discountRepository;
    
    public DiscountCalculationManager(List<DiscountCalculationStrategy> strategies, 
                                    DiscountRepository discountRepository) {
        this.strategies = strategies != null ? strategies : List.of();
        this.discountRepository = discountRepository;
    }
    
    /**
     * Calculate the total discount percentage for a reservation by applying
     * all registered discount strategies.
     * 
     * @param reservation The reservation to calculate discount for
     * @param context Context containing selected promotions and manual discounts
     * @return Total discount percentage (0-100)
     */
    public BigDecimal calculateTotalDiscount(Reservation reservation, DiscountCalculationContext context) {
        if (reservation == null) {
            return BigDecimal.ZERO;
        }
        
        // Get all active discounts from repository
        List<Discount> activeDiscounts = discountRepository.findActive();
        
        // Apply each strategy and accumulate discount percentages
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        for (DiscountCalculationStrategy strategy : strategies) {
            try {
                BigDecimal strategyDiscount = strategy.calculateDiscount(reservation, activeDiscounts, context);
                if (strategyDiscount != null && strategyDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    totalDiscount = totalDiscount.add(strategyDiscount);
                }
            } catch (Exception e) {
                // Log error but continue with other strategies
                System.err.println("Error applying discount strategy " + strategy.getStrategyName() + ": " + e.getMessage());
            }
        }
        
        // Apply business rules: discount cannot exceed 100% or be negative
        if (totalDiscount.compareTo(BigDecimal.valueOf(100)) > 0) {
            totalDiscount = BigDecimal.valueOf(100);
        }
        if (totalDiscount.compareTo(BigDecimal.ZERO) < 0) {
            totalDiscount = BigDecimal.ZERO;
        }
        
        return totalDiscount.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Get the number of registered strategies.
     */
    public int getStrategyCount() {
        return strategies.size();
    }
}
